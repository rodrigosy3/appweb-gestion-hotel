package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.hotel.appHotel.model.Roles;
import com.hotel.appHotel.service.RolesService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping(value = "/admin")
public class RolesController {

    @Autowired
    private RolesService rolesServicio;

    @GetMapping("/roles")
    public String listarRoles(Model modelo) {
        modelo.addAttribute("roles", rolesServicio.getRoles());
        
        return "roles";
    }
    
    @GetMapping("/roles/nuevo")
    public String nuevoRolForm(Model modelo) {
        Roles rol = new Roles();
        
        modelo.addAttribute("roles", rolesServicio.getRoles());
        modelo.addAttribute("rol", rol);
        
        return "form_nuevo_rol";
    }

    @PostMapping("/roles")
    public String crearRol(@ModelAttribute("rol") Roles rol) {
        rol.setNombre(rol.getNombre().toUpperCase());
        rolesServicio.createRol(rol);
        
        return "redirect:/admin/roles";
    }
    
    @GetMapping("/roles/editar/{id}")
    public String editarRolForm(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("roles", rolesServicio.getRoles());
        modelo.addAttribute("rol", rolesServicio.getRolById(id));

        return "form_editar_rol";
    }

    @PostMapping("/roles/{id}")
    public String actualizarRol(@PathVariable Long id, @ModelAttribute("rol") Roles rol , Model modelo) {
        Roles rolExistente = rolesServicio.getRolById(id);
        rolExistente.setId_rol(id);
        rolExistente.setNombre(rol.getNombre().toUpperCase());
        rolExistente.setNivel(rol.getNivel());
        rolExistente.setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        rolesServicio.updateRol(rolExistente);

        return "redirect:/admin/roles";
    }
    
    @GetMapping("/roles/{id}")
    public String eliminarRol(@PathVariable Long id) {
        rolesServicio.deleteRol(id);

        return "redirect:/admin/roles";
    }
}
