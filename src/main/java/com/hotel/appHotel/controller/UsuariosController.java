package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.hotel.appHotel.model.HistorialVetos;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.repository.RolesRepository;
import com.hotel.appHotel.service.HistorialVetosService;
import com.hotel.appHotel.service.UsuariosService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping(value = "/clientes")
public class UsuariosController {

    private static final String CARPETA_BASE = "trabajadores_templates/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "clientes";
    private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_cliente";
    private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_cliente";
    private static final String REDIRECT_LISTAR = "redirect:/clientes";

    @Autowired
    private UsuariosService usuariosServicio;

    @Autowired
    private RolesRepository rolesRepositorio;

    @Autowired
    private HistorialVetosService historialVetosService;

    @GetMapping
    public String listarUsuarios(Model modelo) {
        List<Usuarios> usuariosDesc = usuariosServicio.getUsuarios().stream()
                .filter(usuario -> usuario.getRol().getNombre().equals("CLIENTE"))
                .sorted(Comparator.comparing(Usuarios::getId_usuario).reversed()).collect(Collectors.toList());

        modelo.addAttribute("usuarios", usuariosDesc);

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevoUsuarioForm(Model modelo) {
        List<Usuarios> usuariosDesc = usuariosServicio.getUsuarios().stream()
                .filter(user -> user.getRol().getNombre().equals("CLIENTE"))
                .sorted(Comparator.comparing(Usuarios::getId_usuario).reversed()).collect(Collectors.toList());

        modelo.addAttribute("usuarios", usuariosDesc);

        Usuarios usuario = new Usuarios();
        modelo.addAttribute("usuario", usuario);

        return VIEW_NUEVO;
    }

    @PostMapping
    public String crearUsuario(@ModelAttribute("usuarios") Usuarios usuario) {
        usuario.setRol(rolesRepositorio.findByNombre("CLIENTE"));
        usuario.setNombres(usuario.getNombres().toUpperCase());
        usuario.setApellidos(usuario.getApellidos().toUpperCase());

        usuariosServicio.createUsuario(usuario);

        if (usuario.getEstado_vetado()) {
            Usuarios usuario_admin = usuariosServicio.getUsuarioById(2L) != null ? usuariosServicio.getUsuarioById(2L)
                    : usuariosServicio.getUsuarioById(1L);

            HistorialVetos historialNuevo = new HistorialVetos();

            historialNuevo.setUsuario_responsable(usuario_admin);
            historialNuevo.setUsuario_vetado(usuario);
            historialNuevo.setRazon(usuario.getRazon_vetado());

            historialVetosService.createHistorialVeto(historialNuevo);
        }

        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(@PathVariable Long id, Model modelo) {
        List<Usuarios> usuariosDesc = usuariosServicio.getUsuarios().stream()
                .filter(user -> user.getRol().getNombre().equals("CLIENTE"))
                .sorted(Comparator.comparing(Usuarios::getId_usuario).reversed()).collect(Collectors.toList());

        if (!usuariosServicio.getUsuarioById(id).getRol().getNombre().equals("CLIENTE")) {
            return REDIRECT_LISTAR;
        }

        modelo.addAttribute("usuarios", usuariosDesc);
        modelo.addAttribute("usuario", usuariosServicio.getUsuarioById(id));

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute("usuario") Usuarios usuario, Model modelo) {
        Usuarios usuarioExistente = usuariosServicio.getUsuarioById(id);

        if (usuarioExistente.getRazon_vetado() != usuario.getRazon_vetado()) {
            Usuarios usuario_admin = usuariosServicio.getUsuarioById(2L) != null ? usuariosServicio.getUsuarioById(2L)
                    : usuariosServicio.getUsuarioById(1L);

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

        usuariosServicio.updateUsuario(usuarioExistente);

        return REDIRECT_LISTAR;
    }

    @PostMapping("/vetar/{id}")
    public ResponseEntity<String> vetarCliente(@PathVariable Long id, @RequestBody Usuarios datos) {
        Usuarios usuario = usuariosServicio.getUsuarioById(id);

        if (usuario == null) {
            return ResponseEntity.badRequest().body("Error: No se encontró el cliente.");
        }

        usuario.setEstado_vetado(true);
        usuario.setRazon_vetado(datos.getRazon_vetado());
        usuariosServicio.updateUsuario(usuario);

        Usuarios usuario_admin = usuariosServicio.getUsuarioById(2L) != null ? usuariosServicio.getUsuarioById(2L)
                : usuariosServicio.getUsuarioById(1L);

        HistorialVetos historialNuevo = new HistorialVetos();

        historialNuevo.setUsuario_responsable(usuario_admin);
        historialNuevo.setUsuario_vetado(usuario);
        historialNuevo.setRazon(usuario.getRazon_vetado());

        historialVetosService.createHistorialVeto(historialNuevo);

        return ResponseEntity.ok("Cliente vetado correctamente.");
    }

}
