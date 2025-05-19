package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.appHotel.model.Credenciales;
import com.hotel.appHotel.model.HistorialVetos;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.repository.CredencialesRepository;
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
    private CredencialesRepository repositorioCredenciales;

    @Autowired
    private PdfServiceClientes pdfServiceClientes;

    @GetMapping
    public String listarUsuariosPorPagina(@RequestParam Map<String, Object> params, Model model) {
        int page = params.get("page") != null ? (Integer.parseInt(params.get("page").toString()) - 1) : 0;

        PageRequest pageRequest = PageRequest.of(page, 20);

        Page<Usuarios> pageUsuarios = servicio.getUsuariosNoEliminados(pageRequest);

        int totalPages = pageUsuarios.getTotalPages();

        if (totalPages > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }

        model.addAttribute("usuarios", pageUsuarios.getContent());
        model.addAttribute("actualPage", page + 1);
        model.addAttribute("nextPage", page + 2);
        model.addAttribute("prevPage", page);
        model.addAttribute("lastPage", totalPages);

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevoUsuarioForm(Model modelo) {
        Usuarios usuario = new Usuarios();
        modelo.addAttribute("usuario", usuario);

        return VIEW_NUEVO;
    }

    @PostMapping
    public String crearUsuario(@ModelAttribute("usuarios") Usuarios usuario) {
        usuario.setRol(rolesRepositorio.findByNombre("CLIENTE"));
        usuario.setNombres(usuario.getNombres().toUpperCase());
        usuario.setApellidos(usuario.getApellidos().toUpperCase());

        servicio.createUsuario(usuario);

        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("usuario", servicio.getUsuarioById(id));

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute("usuario") Usuarios usuario, Model modelo) {
        Usuarios usuarioExistente = servicio.getUsuarioById(id);

        if (!usuarioExistente.getRazon_vetado().equals(usuario.getRazon_vetado())) {
            HistorialVetos historialNuevo = new HistorialVetos();

            // Logica para asignar el usuario responsable del cambio de la venta
            // Usuarios usuario_admin = servicio.getUsuarioById(2L) != null ? servicio.getUsuarioById(2L)
            //         : servicio.getUsuarioById(1L);
            String dni = SecurityContextHolder.getContext().getAuthentication().getName();

            Optional<Credenciales> credencialOpt = repositorioCredenciales.buscarPorDni(dni);
            Usuarios usuario_responsable = credencialOpt.get().getUsuario();

            historialNuevo.setUsuario_responsable(usuario_responsable);
            /////////////////////////////////////////////////////////////////////

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
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        servicio.updateUsuario(usuarioExistente);

        return REDIRECT_LISTAR;
    }

    @PostMapping("/vetar/{id}")
    public ResponseEntity<String> vetarCliente(@PathVariable Long id, @RequestBody Usuarios datos) {
        Usuarios usuario = servicio.getUsuarioById(id);

        usuario.setEstado_vetado(true);
        usuario.setRazon_vetado(datos.getRazon_vetado());
        servicio.updateUsuario(usuario);

        HistorialVetos historialNuevo = new HistorialVetos();

        // Logica para asignar el usuario responsable del cambio de la venta
        // Usuarios usuario_admin = servicio.getUsuarioById(2L) != null ? servicio.getUsuarioById(2L)
        //         : servicio.getUsuarioById(1L);
        String dni = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Credenciales> credencialOpt = repositorioCredenciales.buscarPorDni(dni);
        Usuarios usuario_responsable = credencialOpt.get().getUsuario();

        historialNuevo.setUsuario_responsable(usuario_responsable);
        /////////////////////////////////////////////////////////////////////

        historialNuevo.setUsuario_vetado(usuario);
        historialNuevo.setRazon(usuario.getRazon_vetado());

        historialVetosService.createHistorialVeto(historialNuevo);

        return ResponseEntity.ok("El cliente " + usuario.getNombres() + " ha sido vetado.");
    }

    @GetMapping("/exportar-pdf")
    public ResponseEntity<byte[]> exportarVentasPdf(@RequestParam(defaultValue = "1") int page) {
        int pageSize = 20;
        PageRequest pageable = PageRequest.of(page - 1, pageSize);

        Page<Usuarios> usuariosPage = servicio.getUsuariosNoEliminados(pageable);

        byte[] pdf = pdfServiceClientes.generarPdfClientes(usuariosPage.getContent());

        if (pdf == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clientes.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
