package com.hotel.appHotel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";  // Thymeleaf en src/main/resources/templates/login.html
    }

    @PostMapping("/shutdown")
    public void shutdown() {
        System.exit(0);
    }
}
