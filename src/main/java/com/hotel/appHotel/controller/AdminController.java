package com.hotel.appHotel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    private static final String CARPETA_BASE = "templates_admin/";
    private static final String VIEW_AMDIN = CARPETA_BASE + "vistaTablas";
    // private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_venta";
    // private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_venta";
    // private static final String REDIRECT_LISTAR = "redirect:/admin/ventas";

    @GetMapping
    public String vistaTablas() {
        return VIEW_AMDIN;
    }
}
