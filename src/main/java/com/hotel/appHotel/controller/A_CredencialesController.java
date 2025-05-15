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
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.service.CredencialesService;
import com.hotel.appHotel.service.RolesService;
import com.hotel.appHotel.service.UsuariosService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value = "/admin/credenciales")
public class A_CredencialesController {

    private static final String CARPETA_BASE = "templates_credenciales/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "credenciales";
    // private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_credencial";
    private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_credencial";
    private static final String REDIRECT_LISTAR = "redirect:/admin/credenciales";

    @Autowired
    private CredencialesService servicio;

    @Autowired
    private UsuariosService usuariosServicio;

    @Autowired
    private RolesService rolesServicio;

    @GetMapping
    public String listarCredenciales(Model modelo) {
        modelo.addAttribute("credenciales", obtenerCredrenciales());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());

        return VIEW_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarCredencialForm(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("credenciales", obtenerCredrenciales());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());
        modelo.addAttribute("credencial", servicio.getCredencialById(id));

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarCredencial(@PathVariable Long id, @ModelAttribute("credencial") Credenciales credencial,
            Model modelo) {
        Credenciales credencialExistente = servicio.getCredencialById(id);

        credencialExistente.setContrasena(credencial.getContrasena());
        credencialExistente
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        servicio.updateCredencial(credencialExistente);

        return REDIRECT_LISTAR;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarCredencial(@PathVariable Long id) {
        Credenciales credencialExistente = servicio.getCredencialById(id);
        Usuarios usuarioNuevoRol = credencialExistente.getUsuario();

        usuarioNuevoRol.setRol(rolesServicio.getByName("CLIENTE"));

        usuariosServicio.updateUsuario(usuarioNuevoRol);

        credencialExistente.setEliminado(true);
        servicio.updateCredencial(credencialExistente);

        return REDIRECT_LISTAR;
    }

    private List<Credenciales> obtenerCredrenciales() {
        return servicio.getCredenciales()
                .stream()
                .filter(credencial -> !credencial.isEliminado())
                .sorted(Comparator.comparing(Credenciales::getId_credencial).reversed())
                .collect(Collectors.toList());
    }

    private HashMap<Long, LocalDateTime> obtenerFechasCreacionEnLocalDateTime() {
        HashMap<Long, LocalDateTime> fechasCreacion = new HashMap<>();

        for (Credenciales credencial : obtenerCredrenciales()) {
            fechasCreacion.put(credencial.getId_credencial(), LocalDateTime.parse(credencial.getFecha_creacion()));
        }

        return fechasCreacion;
    }
}
