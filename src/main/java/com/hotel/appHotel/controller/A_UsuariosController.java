package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.hotel.appHotel.model.Credenciales;
import com.hotel.appHotel.model.Roles;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.repository.RolesRepository;
import com.hotel.appHotel.service.CredencialesService;
import com.hotel.appHotel.service.UsuariosService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping(value = "/admin/usuarios")
public class A_UsuariosController {

    @Autowired
    private UsuariosService usuariosServicio;

    @Autowired
    private RolesRepository rolesRepositorio;

    @Autowired
    private CredencialesService credencialesServicio;

    @GetMapping
    public String listarUsuarios(Model modelo) {
        List<Usuarios> usuariosDesc = usuariosServicio.getUsuarios();
        usuariosDesc = usuariosDesc.stream().sorted(Comparator.comparing(Usuarios::getId_usuario).reversed()).collect(Collectors.toList());

        modelo.addAttribute("usuarios", usuariosDesc);

        return "templates_usuarios/usuarios";
    }

    @GetMapping("/nuevo")
    public String nuevoUsuarioForm(Model modelo) {
        Usuarios usuario = new Usuarios();
        List<Roles> roles = rolesRepositorio.findAll();

        modelo.addAttribute("roles", roles);
        modelo.addAttribute("usuarios", usuariosServicio.getUsuarios());
        modelo.addAttribute("usuario", usuario);

        return "templates_usuarios/form_nuevo_usuario";
    }

    @PostMapping
    public String crearUsuario(@ModelAttribute("usuarios") Usuarios usuario) {
        usuario.setNombres(usuario.getNombres().toUpperCase());
        usuario.setApellidos(usuario.getApellidos().toUpperCase());
        usuariosServicio.createUsuario(usuario);

        if (usuario.getRol().getNivel() != 0) {
            List<Credenciales> credenciales = credencialesServicio.getCredenciales();
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

        return "redirect:/admin/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(@PathVariable Long id, Model modelo) {
        List<Roles> roles = rolesRepositorio.findAll();

        modelo.addAttribute("roles", roles);
        modelo.addAttribute("usuarios", usuariosServicio.getUsuarios());
        modelo.addAttribute("usuario", usuariosServicio.getUsuarioById(id));

        return "templates_usuarios/form_editar_usuario";
    }

    @PostMapping("/{id}")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute("usuario") Usuarios usuario, Model modelo) {
        Usuarios usuarioExistente = usuariosServicio.getUsuarioById(id);
        List<Credenciales> credenciales = credencialesServicio.getCredenciales();

        usuarioExistente.setNombres(usuario.getNombres().toUpperCase());
        usuarioExistente.setApellidos(usuario.getApellidos().toUpperCase());
        usuarioExistente.setEdad(usuario.getEdad());
        usuarioExistente.setDni(usuario.getDni());
        usuarioExistente.setCelular(usuario.getCelular());
        usuarioExistente.setEstado_vetado(usuario.getEstado_vetado());
        usuarioExistente.setRazon_vetado(usuario.getRazon_vetado());
        usuarioExistente.setRol(usuario.getRol());
        usuarioExistente
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (usuarioExistente.getRol().getNivel() != 0) {
            boolean credencialExistente = true;

            for (Credenciales credencial : credenciales) {
                if (usuarioExistente.getDni().equals(credencial.getUsuario().getDni())) {
                    credencialExistente = false;
                }
            }

            if (credencialExistente) {
                Credenciales credencialNuevo = new Credenciales();

                credencialNuevo.setUsuario(usuarioExistente);
                credencialNuevo.setContrasena(usuarioExistente.getDni());

                credencialesServicio.createCredencial(credencialNuevo);
            }
        } else {
            Credenciales credencialEliminar = new Credenciales();

            for (Credenciales credencial : credenciales) {
                if (usuarioExistente.getId_usuario().equals(credencial.getUsuario().getId_usuario())) {
                    credencialEliminar = credencial;
                    credencialesServicio.deleteCredencial(credencialEliminar.getId_credencial());
                }
            }
        }

        usuariosServicio.updateUsuario(usuarioExistente);

        return "redirect:/admin/usuarios";
    }

    @GetMapping("/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuariosServicio.deleteUsuario(id);

        return "redirect:/admin/usuarios";
    }
}
