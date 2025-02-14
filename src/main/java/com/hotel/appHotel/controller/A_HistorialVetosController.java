package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.hotel.appHotel.model.HistorialVetos;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.service.HistorialVetosService;
import com.hotel.appHotel.service.UsuariosService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping(value = "/admin/historialVetos")
public class A_HistorialVetosController {

    @Autowired
    private HistorialVetosService historialVetosServicio;

    @Autowired
    private UsuariosService usuariosServicio;

    @GetMapping
    public String listarHistorialVetos(Model modelo) {
        List<HistorialVetos> historialVetosDesc = historialVetosServicio.getHistorialVetos().stream()
                .sorted(Comparator.comparing(HistorialVetos::getId_historial_veto).reversed())
                .collect(Collectors.toList());

        modelo.addAttribute("historialVetos", historialVetosDesc);

        return "templates_historialVetos/historialVetos";
    }

    @GetMapping("/nuevo")
    public String nuevoHistorialVetoForm(Model modelo, RedirectAttributes redirectAttributes) {
        HistorialVetos historialVeto = new HistorialVetos();
        List<Usuarios> usuarios = usuariosServicio.getUsuarios();
        List<Usuarios> usuarios_responsables = new ArrayList<>();
        List<Usuarios> usuarios_clientes = new ArrayList<>();

        for (Usuarios usuario : usuarios) {
            if (usuario.getRol().getNivel() == 0) {
                usuarios_clientes.add(usuario);
            } else {
                usuarios_responsables.add(usuario);
            }
        }

        if (usuarios_responsables.isEmpty() || usuarios_clientes.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay usuarios con roles adecuados para crear un historial de vetos.");

            return "redirect:/admin/historialVetos";
        } else {
            List<HistorialVetos> historialVetosDesc = historialVetosServicio.getHistorialVetos().stream()
                .sorted(Comparator.comparing(HistorialVetos::getId_historial_veto).reversed())
                .collect(Collectors.toList());

            modelo.addAttribute("usuarios_responsables", usuarios_responsables);
            modelo.addAttribute("usuarios_clientes", usuarios_clientes);
            modelo.addAttribute("historialVetos", historialVetosDesc);
            modelo.addAttribute("historialVeto", historialVeto);

            return "templates_historialVetos/form_nuevo_historialVeto";
        }
    }

    @PostMapping
    public String crearHistorialVetos(@ModelAttribute("historialVetos") HistorialVetos historialVeto) {
        Usuarios usuarioExistente = usuariosServicio.getUsuarioById(historialVeto.getUsuario_vetado().getId_usuario());

        if (!usuarioExistente.getEstado_vetado()) {
            usuarioExistente.setEstado_vetado(true);
        }

        usuarioExistente.setRazon_vetado(historialVeto.getRazon());

        usuariosServicio.updateUsuario(usuarioExistente);
        historialVetosServicio.createHistorialVeto(historialVeto);
        return "redirect:/admin/historialVetos";
    }

    @GetMapping("/editar/{id}")
    public String editarHistorialVetosForm(@PathVariable Long id, Model modelo, RedirectAttributes redirectAttributes) {
        List<Usuarios> usuarios = usuariosServicio.getUsuarios();
        List<Usuarios> usuarios_clientes = new ArrayList<>();
        List<Usuarios> usuarios_responsables = new ArrayList<>();

        for (Usuarios usuario : usuarios) {
            if (usuario.getRol().getNivel() == 0) {
                usuarios_clientes.add(usuario);
            } else {
                usuarios_responsables.add(usuario);
            }
        }

        if (usuarios_responsables.isEmpty() || usuarios_clientes.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay usuarios con roles adecuados para crear un historial de vetos.");
            return "redirect:/admin/historialVetos";
        } else {
            List<HistorialVetos> historialVetosDesc = historialVetosServicio.getHistorialVetos().stream()
                .sorted(Comparator.comparing(HistorialVetos::getId_historial_veto).reversed())
                .collect(Collectors.toList());

            modelo.addAttribute("usuarios_clientes", usuarios_clientes);
            modelo.addAttribute("usuarios_responsables", usuarios_responsables);
            modelo.addAttribute("historialVetos", historialVetosDesc);
            modelo.addAttribute("historialVeto", historialVetosServicio.getHistorialVetoById(id));

            return "templates_historialVetos/form_editar_historialVeto";
        }
    }

    @PostMapping("/{id}")
    public String actualizarHistorialVeto(@PathVariable Long id,
            @ModelAttribute("historialVeto") HistorialVetos historialVeto, Model modelo) {
        HistorialVetos historialVetoExistente = historialVetosServicio.getHistorialVetoById(id);
        Usuarios usuarioExistente = usuariosServicio.getUsuarioById(historialVeto.getUsuario_vetado().getId_usuario());

        historialVetoExistente.setUsuario_vetado(historialVeto.getUsuario_vetado());
        historialVetoExistente.setUsuario_responsable(historialVeto.getUsuario_responsable());
        historialVetoExistente.setRazon(historialVeto.getRazon());
        historialVetoExistente
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (!usuarioExistente.getEstado_vetado()) {
            usuarioExistente.setEstado_vetado(true);
        }

        usuarioExistente.setRazon_vetado(historialVeto.getRazon());

        usuariosServicio.updateUsuario(usuarioExistente);
        historialVetosServicio.updateHistorialVeto(historialVetoExistente);

        return "redirect:/admin/historialVetos";
    }

    @GetMapping("/{id}")
    public String eliminarhistorialVeto(@PathVariable Long id) {
        historialVetosServicio.deleteHistorialVeto(id);

        return "redirect:/admin/historialVetos";
    }
}