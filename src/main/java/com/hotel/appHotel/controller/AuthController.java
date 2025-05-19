package com.hotel.appHotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.hotel.appHotel.service.BackupService;

@Controller
public class AuthController {

    @Autowired
    private BackupService backupService;

    @GetMapping("/login")
    public String login() {
        return "login";  // Thymeleaf en src/main/resources/templates/login.html
    }

    @PostMapping("/shutdown")
    public String apagarApp() {
        new Thread(() -> {
            try {
                Thread.sleep(3000); // Espera 3 segundos

                backupService.generarBackup(); // Genera el backup antes de cerrar

                System.exit(0); // Apaga la app después de mostrar vista
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        return "offApp"; // Muestra la animación
    }

}
