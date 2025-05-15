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

import com.hotel.appHotel.model.Roles;
import com.hotel.appHotel.service.RolesService;

@Controller
@RequestMapping(value = "/admin/roles")
public class A_RolesController {

    private static final String CARPETA_BASE = "templates_roles/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "roles";
    private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_rol";
    private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_rol";
    private static final String REDIRECT_LISTAR = "redirect:/admin/roles";

    @Autowired
    private RolesService servicio;

    @GetMapping
    public String listarRoles(Model modelo) {
        modelo.addAttribute("roles", obtenerRoles());
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevoRolForm(Model modelo) {
        Roles rol = new Roles(); 

        modelo.addAttribute("roles", obtenerRoles());
        modelo.addAttribute("rol", rol);
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());

        return VIEW_NUEVO;
    }

    @PostMapping
    public String crearRol(@ModelAttribute("rol") Roles rol) {
        rol.setNombre(rol.getNombre().toUpperCase());

        servicio.createRol(rol);

        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarRolForm(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("roles", obtenerRoles());
        modelo.addAttribute("rol", servicio.getRolById(id));
        modelo.addAttribute("fechasCreacion", obtenerFechasCreacionEnLocalDateTime());

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarRol(@PathVariable Long id, @ModelAttribute("rol") Roles rol, Model modelo) {
        Roles rolExistente = servicio.getRolById(id);

        rolExistente.setNombre(rol.getNombre().toUpperCase());
        rolExistente.setNivel(rol.getNivel());
        rolExistente.setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        servicio.updateRol(rolExistente);

        return REDIRECT_LISTAR;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarRol(@PathVariable Long id) {
        Roles rolExistente = servicio.getRolById(id);

        rolExistente.setEliminado(true);

        servicio.updateRol(rolExistente);

        return REDIRECT_LISTAR;
    }

    private List<Roles> obtenerRoles() {
        return servicio.getRoles()
                .stream()
                .filter(rol -> !rol.isEliminado())
                .sorted(Comparator.comparing(Roles::getNivel))
                .collect(Collectors.toList());
    }

    private HashMap<Long, LocalDateTime> obtenerFechasCreacionEnLocalDateTime() {
        HashMap<Long, LocalDateTime> fechasCreacion = new HashMap<>();

        for (Roles rol : obtenerRoles()) {
            fechasCreacion.put(rol.getId_rol(), LocalDateTime.parse(rol.getFecha_creacion()));
        }

        return fechasCreacion;
    }
}
