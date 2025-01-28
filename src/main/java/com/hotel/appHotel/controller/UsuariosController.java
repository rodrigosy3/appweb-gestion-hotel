package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.hotel.appHotel.model.Roles;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.repository.RolesRepository;
import com.hotel.appHotel.service.UsuariosService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping(value = "/admin/usuarios")
public class UsuariosController {

    @Autowired
    private UsuariosService usuariosServicio;

    @Autowired
    private RolesRepository rolesRepositorio;

    @GetMapping
    public String listarUsuarios(Model modelo) {
        modelo.addAttribute("usuarios", usuariosServicio.getUsuarios());
        
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
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute("usuario") Usuarios usuario , Model modelo) {
        Usuarios usuarioExistente = usuariosServicio.getUsuarioById(id);
        usuarioExistente.setId_usuario(id);
        usuarioExistente.setNombres(usuario.getNombres().toUpperCase());
        usuarioExistente.setApellidos(usuario.getApellidos().toUpperCase());
        usuarioExistente.setEdad(usuario.getEdad());
        usuarioExistente.setDni(usuario.getDni());
        usuarioExistente.setCelular(usuario.getCelular());
        usuarioExistente.setRol(usuario.getRol());
        usuarioExistente.setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        usuariosServicio.updateUsuario(usuarioExistente);

        return "redirect:/admin/usuarios";
    }
    
    @GetMapping("/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuariosServicio.deleteUsuario(id);

        return "redirect:/admin/usuarios";
    }
}
