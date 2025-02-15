package com.hotel.appHotel.controller;

import java.io.File;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalController {

    @ModelAttribute("dbSize")
    public String getDatabaseSize() {
        String ruta = System.getProperty("user.home") + "/Documents/AppHotelDatos/db_hotel.db";
        File file = new File(ruta); // Asegúrate de que esta ruta sea correcta
        long sizeInKB = file.exists() ? file.length() / 1024 : 0; // Convertir a KB
        return sizeInKB + " KB";
    }
}

