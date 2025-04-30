package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hotel.appHotel.model.HistorialVetos;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.repository.RolesRepository;
import com.hotel.appHotel.service.HistorialVetosService;
import com.hotel.appHotel.service.PdfServiceClientes;
import com.hotel.appHotel.service.UsuariosService;

@Controller
@RequestMapping(value = "/clientes")
public class UsuariosController {

    private static final String CARPETA_BASE = "trabajadores_templates/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "clientes";
    private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_cliente";
    private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_cliente";
    private static final String REDIRECT_LISTAR = "redirect:/clientes";

    @Autowired
    private UsuariosService servicio;

    @Autowired
    private RolesRepository rolesRepositorio;

    @Autowired
    private HistorialVetosService historialVetosService;

    @Autowired
    private PdfServiceClientes pdfServiceClientes;

    @GetMapping
    public String listarUsuarios(Model modelo) {
        modelo.addAttribute("usuarios", obtenerUsuarios());

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevoUsuarioForm(Model modelo) {
        modelo.addAttribute("usuarios", obtenerUsuarios());

        Usuarios usuario = new Usuarios();
        modelo.addAttribute("usuario", usuario);

        return VIEW_NUEVO;
    }

    @PostMapping
    public String crearUsuario(@ModelAttribute("usuarios") Usuarios usuario) {
        usuario.setRol(rolesRepositorio.findByNombre("CLIENTE"));
        usuario.setNombres(usuario.getNombres().toUpperCase());
        usuario.setApellidos(usuario.getApellidos().toUpperCase());

        if (usuario.getEstado_vetado()) {
            Usuarios usuario_admin = servicio.getUsuarioById(2L) != null ? servicio.getUsuarioById(2L)
                    : servicio.getUsuarioById(1L);

            HistorialVetos historialNuevo = new HistorialVetos();

            historialNuevo.setUsuario_responsable(usuario_admin);
            historialNuevo.setUsuario_vetado(usuario);
            historialNuevo.setRazon(usuario.getRazon_vetado());

            historialVetosService.createHistorialVeto(historialNuevo);
        }

        servicio.createUsuario(usuario);

        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("usuarios", obtenerUsuarios());
        modelo.addAttribute("usuario", servicio.getUsuarioById(id));

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute("usuario") Usuarios usuario, Model modelo) {
        Usuarios usuarioExistente = servicio.getUsuarioById(id);

        if (!usuarioExistente.getRazon_vetado().equals(usuario.getRazon_vetado())) {
            Usuarios usuario_admin = servicio.getUsuarioById(2L) != null ? servicio.getUsuarioById(2L)
                    : servicio.getUsuarioById(1L);

            HistorialVetos historialNuevo = new HistorialVetos();

            historialNuevo.setUsuario_responsable(usuario_admin);
            historialNuevo.setUsuario_vetado(usuarioExistente);
            historialNuevo.setRazon(usuario.getRazon_vetado());

            historialVetosService.createHistorialVeto(historialNuevo);
        }

        usuarioExistente.setNombres(usuario.getNombres().toUpperCase());
        usuarioExistente.setApellidos(usuario.getApellidos().toUpperCase());
        usuarioExistente.setEdad(usuario.getEdad());
        usuarioExistente.setDni(usuario.getDni());
        usuarioExistente.setCelular(usuario.getCelular());
        usuarioExistente.setEstado_vetado(usuario.getEstado_vetado());
        usuarioExistente.setRazon_vetado(usuario.getRazon_vetado());

        usuarioExistente
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        servicio.updateUsuario(usuarioExistente);

        return REDIRECT_LISTAR;
    }

    @PostMapping("/vetar/{id}")
    public ResponseEntity<String> vetarCliente(@PathVariable Long id, @RequestBody Usuarios datos) {
        Usuarios usuario = servicio.getUsuarioById(id);

        usuario.setEstado_vetado(true);
        usuario.setRazon_vetado(datos.getRazon_vetado());
        servicio.updateUsuario(usuario);

        Usuarios usuario_admin = servicio.getUsuarioById(2L) != null ? servicio.getUsuarioById(2L)
                : servicio.getUsuarioById(1L);

        HistorialVetos historialNuevo = new HistorialVetos();

        historialNuevo.setUsuario_responsable(usuario_admin);
        historialNuevo.setUsuario_vetado(usuario);
        historialNuevo.setRazon(usuario.getRazon_vetado());

        historialVetosService.createHistorialVeto(historialNuevo);

        return ResponseEntity.ok("El cliente " + usuario.getNombres() + " ha sido vetado.");
    }

    @GetMapping("/exportar-pdf")
    public ResponseEntity<byte[]> exportarVentasPdf() {
        byte[] pdf = pdfServiceClientes.generarPdfClientes(obtenerUsuarios());

        if (pdf == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clientes.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private List<Usuarios> obtenerUsuarios() {
        return servicio.getUsuarios()
                .stream()
                .filter(usuario -> !usuario.isEliminado() && usuario.getRol().getNivel() == 0)
                .sorted(Comparator.comparing(Usuarios::getId_usuario).reversed())
                .collect(Collectors.toList());
    }
}
