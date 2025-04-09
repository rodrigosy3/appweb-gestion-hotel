package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private static final String CARPETA_BASE = "templates_historialVetos/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "historialVetos";
    private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_historialVeto";
    private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_historialVeto";
    private static final String REDIRECT_LISTAR = "redirect:/admin/historialVetos";

    @Autowired
    private HistorialVetosService servicio;

    @Autowired
    private UsuariosService usuariosServicio;

    @GetMapping
    public String listarHistorialVetos(Model modelo) {
        modelo.addAttribute("historialVetos", obtenerHistorialVetos());

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevoHistorialVetoForm(Model modelo, RedirectAttributes redirectAttributes) {
        HistorialVetos historialVeto = new HistorialVetos();
        List<Usuarios> usuarios_responsables = obtenerUsuarios()
                .stream()
                .filter(usuario -> usuario.getRol().getNivel() != 0)
                .collect(Collectors.toList());
        List<Usuarios> usuarios_clientes = obtenerUsuarios()
                .stream()
                .filter(usuario -> usuario.getRol().getNivel() == 0)
                .collect(Collectors.toList());

        if (usuarios_responsables.isEmpty() || usuarios_clientes.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay usuarios con roles adecuados para crear un historial de vetos.");

            return REDIRECT_LISTAR;
        } else {
            modelo.addAttribute("usuarios_responsables", usuarios_responsables);
            modelo.addAttribute("usuarios_clientes", usuarios_clientes);
            modelo.addAttribute("historialVetos", obtenerHistorialVetos());
            modelo.addAttribute("historialVeto", historialVeto);

            return VIEW_NUEVO;
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
        servicio.createHistorialVeto(historialVeto);
        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarHistorialVetosForm(@PathVariable Long id, Model modelo, RedirectAttributes redirectAttributes) {
        List<Usuarios> usuarios_responsables = obtenerUsuarios()
                .stream()
                .filter(usuario -> usuario.getRol().getNivel() != 0)
                .collect(Collectors.toList());
        List<Usuarios> usuarios_clientes = obtenerUsuarios()
                .stream()
                .filter(usuario -> usuario.getRol().getNivel() == 0)
                .collect(Collectors.toList());

        if (usuarios_responsables.isEmpty() || usuarios_clientes.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay usuarios con roles adecuados para crear un historial de vetos.");
            return REDIRECT_LISTAR;
        } else {
            modelo.addAttribute("usuarios_clientes", usuarios_clientes);
            modelo.addAttribute("usuarios_responsables", usuarios_responsables);
            modelo.addAttribute("historialVetos", obtenerHistorialVetos());
            modelo.addAttribute("historialVeto", servicio.getHistorialVetoById(id));

            return VIEW_EDITAR;
        }
    }

    @PostMapping("/{id}")
    public String actualizarHistorialVeto(@PathVariable Long id,
            @ModelAttribute("historialVeto") HistorialVetos historialVeto, Model modelo) {
        HistorialVetos historialVetoExistente = servicio.getHistorialVetoById(id);
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
        servicio.updateHistorialVeto(historialVetoExistente);

        return REDIRECT_LISTAR;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarhistorialVeto(@PathVariable Long id) {
        HistorialVetos historialVetoExistente = servicio.getHistorialVetoById(id);
        Usuarios usuarioExistente = usuariosServicio.getUsuarioById(historialVetoExistente.getUsuario_vetado().getId_usuario());

        usuarioExistente.setEstado_vetado(false);
        usuarioExistente.setRazon_vetado("");

        historialVetoExistente.setEliminado(true);

        usuariosServicio.updateUsuario(usuarioExistente);
        servicio.updateHistorialVeto(historialVetoExistente);

        return REDIRECT_LISTAR;
    }

    private List<HistorialVetos> obtenerHistorialVetos() {
        return servicio.getHistorialVetos()
                .stream()
                .filter(historialVeto -> !historialVeto.isEliminado())
                .sorted(Comparator.comparing(HistorialVetos::getId_historial_veto).reversed())
                .collect(Collectors.toList());
    }

    private List<Usuarios> obtenerUsuarios() {
        return usuariosServicio.getUsuarios()
                .stream()
                .filter(usuario -> !usuario.isEliminado())
                .collect(Collectors.toList());
    }
}