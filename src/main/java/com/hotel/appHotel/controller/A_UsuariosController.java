package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hotel.appHotel.model.Credenciales;
import com.hotel.appHotel.model.Roles;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.service.CredencialesService;
import com.hotel.appHotel.service.RolesService;
import com.hotel.appHotel.service.UsuariosService;

@Controller
@RequestMapping(value = "/admin/usuarios")
public class A_UsuariosController {

    private static final String CARPETA_BASE = "templates_usuarios/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "usuarios";
    private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_usuario";
    private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_usuario";
    private static final String REDIRECT_LISTAR = "redirect:/admin/usuarios";

    @Autowired
    private UsuariosService servicio;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private CredencialesService credencialesServicio;

    @GetMapping
    public String listarUsuarios(Model modelo) {
        modelo.addAttribute("usuarios", obtenerUsuarios());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevoUsuarioForm(Model modelo) {
        Usuarios usuario = new Usuarios();

        modelo.addAttribute("roles", obtenerRoles());
        modelo.addAttribute("usuarios", obtenerUsuarios());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());
        modelo.addAttribute("usuario", usuario);

        return VIEW_NUEVO;
    }

    @PostMapping
    public String crearUsuario(@ModelAttribute("usuarios") Usuarios usuario) {
        usuario.setNombres(usuario.getNombres().toUpperCase());
        usuario.setApellidos(usuario.getApellidos().toUpperCase());

        servicio.createUsuario(usuario);

        if (usuario.getRol().getNivel() != 0) asignarCredenciales(usuario);

        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("roles", obtenerRoles());
        modelo.addAttribute("usuarios", obtenerUsuarios());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());
        modelo.addAttribute("usuario", servicio.getUsuarioById(id));

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute("usuario") Usuarios usuario, Model modelo) {
        Usuarios usuarioExistente = servicio.getUsuarioById(id);

        usuarioExistente.setNombres(usuario.getNombres().toUpperCase());
        usuarioExistente.setApellidos(usuario.getApellidos().toUpperCase());
        usuarioExistente.setEdad(usuario.getEdad());
        usuarioExistente.setDni(usuario.getDni());
        usuarioExistente.setCelular(usuario.getCelular());
        usuarioExistente.setEstado_vetado(usuario.getEstado_vetado());
        usuarioExistente.setRazon_vetado(usuario.getRazon_vetado());
        usuarioExistente.setRol(usuario.getRol());
        usuarioExistente
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        if (usuarioExistente.getRol().getNivel() != 0) {
            asignarCredenciales(usuarioExistente);
        } else {
            List<Credenciales> credenciales = obtenerCredrenciales();

            for (Credenciales credencial : credenciales) {
                if (usuarioExistente.getId_usuario().equals(credencial.getUsuario().getId_usuario())) {
                    Credenciales credencialEliminar = credencial;
                    credencialEliminar.setEliminado(true);

                    credencialesServicio.updateCredencial(credencialEliminar);
                    break;
                }
            }
        }

        servicio.updateUsuario(usuarioExistente);

        return REDIRECT_LISTAR;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        Usuarios usuarioExistente = servicio.getUsuarioById(id);
        List<Credenciales> credenciales = obtenerCredrenciales();

        for (Credenciales credencial : credenciales) {
            if (usuarioExistente.getId_usuario().equals(credencial.getUsuario().getId_usuario())) {
                Credenciales credencialEliminar = credencial;
                credencialEliminar.setEliminado(true);

                credencialesServicio.updateCredencial(credencialEliminar);
                break;
            }
        }

        usuarioExistente.setEliminado(true);

        servicio.updateUsuario(usuarioExistente);

        return REDIRECT_LISTAR;
    }

    private List<Usuarios> obtenerUsuarios() {
        return servicio.getUsuarios()
                .stream()
                .filter(usuario -> !usuario.isEliminado())
                .sorted(Comparator.comparing(Usuarios::getId_usuario).reversed())
                .collect(Collectors.toList());
    }

    private List<Roles> obtenerRoles() {
        return rolesService.getRoles()
                .stream()
                .filter(rol -> !rol.isEliminado())
                .sorted(Comparator.comparing(Roles::getNivel))
                .collect(Collectors.toList());
    }

    private List<Credenciales> obtenerCredrenciales() {
        return credencialesServicio.getCredenciales()
                .stream()
                .filter(credencial -> !credencial.isEliminado())
                .collect(Collectors.toList());
    }

    private void asignarCredenciales(Usuarios usuario) {
        List<Credenciales> credenciales = obtenerCredrenciales();
        boolean credencialExistente = true;

        for (Credenciales credencial : credenciales) {
            if (usuario.getDni().equals(credencial.getUsuario().getDni())) {
                credencialExistente = false;
            }
        }

        if (credencialExistente) {
            Credenciales credencialNuevo = new Credenciales();

            credencialNuevo.setUsuario(usuario);
            credencialNuevo.setContrasena(usuario.getDni());

            credencialesServicio.createCredencial(credencialNuevo);
        }
    }

    private HashMap<Long, LocalDateTime> obtenerFechasCreacionEnLocalDateTime() {
        HashMap<Long, LocalDateTime> fechasCreacion = new HashMap<>();

        for (Usuarios usuario : obtenerUsuarios()) {
            fechasCreacion.put(usuario.getId_usuario(), LocalDateTime.parse(usuario.getFecha_creacion()));
        }

        return fechasCreacion;
    }
}
