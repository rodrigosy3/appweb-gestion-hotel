package com.hotel.appHotel.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.appHotel.model.Habitaciones;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.model.Ventas;
import com.hotel.appHotel.model.VentasClientesHabitacion;
import com.hotel.appHotel.service.HabitacionesService;
import com.hotel.appHotel.service.UsuariosService;
import com.hotel.appHotel.service.VentasClientesHabitacionService;
import com.hotel.appHotel.service.VentasService;

@Controller
public class IndexController {
    private static final String VIEW_INICIO = "index";

    @Autowired
    HabitacionesService servicioHabitaciones;

    @Autowired
    UsuariosService servicioUsuarios;

    @Autowired
    VentasService servicioVentas;

    @Autowired
    VentasClientesHabitacionService servicioVentasClientesHabitacion;
    
    @GetMapping("/inicio")
    public String getDatos(@RequestParam(value = "fechaFiltro", required = false) String fechaFiltro, Model modelo) {
        List<Habitaciones> habitaciones = servicioHabitaciones.getHabitaciones();
        List<Usuarios> usuarios = servicioUsuarios.getUsuarios();
        List<Ventas> ventas = servicioVentas.getVentas();
        List<VentasClientesHabitacion> ventasClientesHabitacion = servicioVentasClientesHabitacion.getVentasClientesHabitacion();

        List<Usuarios> usuarios_clientes = usuarios.stream().filter(usuario -> usuario.getRol().getNivel().equals(0)).collect(Collectors.toList());
        List<Usuarios> usuarios_personal = usuarios.stream().filter(usuario -> !usuario.getRol().getNivel().equals(0)).collect(Collectors.toList());
        
        List<Habitaciones> hab_antiguas = habitaciones.stream().filter(habitacion -> !habitacion.getCategoria().equals("ANTIGUO")).collect(Collectors.toList());
        List<Habitaciones> hab_modernas = habitaciones.stream().filter(habitacion -> !habitacion.getCategoria().equals("MODERNO")).collect(Collectors.toList());

        modelo.addAttribute("usuarios_clientes", usuarios_clientes);
        modelo.addAttribute("usuarios_personal", usuarios_personal);

        modelo.addAttribute("hab_antiguas", hab_antiguas);
        modelo.addAttribute("hab_modernas", hab_modernas);
        
        modelo.addAttribute("ventas", ventas);
        modelo.addAttribute("ventasClientesHabitacion", ventasClientesHabitacion);

        return VIEW_INICIO;
    }


}
