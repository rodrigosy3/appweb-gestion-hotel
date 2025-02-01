package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.hotel.appHotel.model.Credenciales;
import com.hotel.appHotel.model.Roles;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.service.CredencialesService;
import com.hotel.appHotel.service.RolesService;
import com.hotel.appHotel.service.UsuariosService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequestMapping(value = "/admin/credenciales")
public class A_CredencialesController {

    @Autowired
    private CredencialesService credencialesServicio;

    @Autowired
    private UsuariosService usuariosServicio;

    @Autowired
    private RolesService rolesService;

    @GetMapping
    public String listarCredenciales(Model modelo) {
        modelo.addAttribute("credenciales", credencialesServicio.getCredenciales());

        return "templates_credenciales/credenciales";
    }

    @GetMapping("/editar/{id}")
    public String editarCredencialForm(@PathVariable Long id, Model modelo) {        
        modelo.addAttribute("credenciales", credencialesServicio.getCredenciales());
        modelo.addAttribute("credencial", credencialesServicio.getCredencialById(id));
        System.out.println(credencialesServicio.getCredencialById(id));

        return "templates_credenciales/form_editar_credencial";
    }

    @PostMapping("/{id}")
    public String actualizarCredencial(@PathVariable Long id, @ModelAttribute("credencial") Credenciales credencial,
            Model modelo) {
        Credenciales credencialExistente = credencialesServicio.getCredencialById(id);
        
        credencialExistente.setContrasena(credencial.getContrasena());
        credencialExistente
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        credencialesServicio.updateCredencial(credencialExistente);

        return "redirect:/admin/credenciales";
    }

    @GetMapping("/{id}")
    public String eliminarCredencial(@PathVariable Long id) {
        Credenciales credencialExistente = credencialesServicio.getCredencialById(id);
        Usuarios usuarioNuevoRol = credencialExistente.getUsuario();
        Roles rolCliente = rolesService.getByName("CLIENTE");

        usuarioNuevoRol.setRol(rolCliente);

        usuariosServicio.updateUsuario(usuarioNuevoRol);
        credencialesServicio.deleteCredencial(id);

        return "redirect:/admin/credenciales";
    }
}
