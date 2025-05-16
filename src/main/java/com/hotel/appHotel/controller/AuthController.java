package com.hotel.appHotel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";  // Thymeleaf en src/main/resources/templates/login.html
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso_denegado";
    }
}
