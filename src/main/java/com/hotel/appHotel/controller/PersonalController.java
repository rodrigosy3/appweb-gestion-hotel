package com.hotel.appHotel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PersonalController {
    private static final String VIEW_INICIO = "index";

    @GetMapping("/inicio")
    public String getDatos() {
        return VIEW_INICIO;
    }
}
