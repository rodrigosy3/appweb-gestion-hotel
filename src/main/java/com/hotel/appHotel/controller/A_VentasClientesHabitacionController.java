package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.model.Ventas;
import com.hotel.appHotel.model.VentasClientesHabitacion;
import com.hotel.appHotel.service.UsuariosService;
import com.hotel.appHotel.service.VentasClientesHabitacionService;
import com.hotel.appHotel.service.VentasService;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping(value = "/admin/ventasClientesHabitacion")
public class A_VentasClientesHabitacionController {

    private static final String CARPETA_BASE = "templates_ventasClientesHabitacion/";
    private static final String VIEW_LISTAR = CARPETA_BASE + "ventasClientesHabitacion";
    private static final String VIEW_NUEVO = CARPETA_BASE + "form_nuevo_ventaClienteHabitacion";
    private static final String VIEW_EDITAR = CARPETA_BASE + "form_editar_ventaClienteHabitacion";
    private static final String REDIRECT_LISTAR = "redirect:/admin/ventasClientesHabitacion";

    @Autowired
    private VentasClientesHabitacionService servicio;

    @Autowired
    private UsuariosService usuariosServicio;

    @Autowired
    private VentasService ventasServicio;

    @GetMapping
    public String listarVentasClientesHabitacion(Model modelo) {
        modelo.addAttribute("ventasClientesHabitacion", obtenerVentasClientesHabitacion());

        return VIEW_LISTAR;
    }

    @GetMapping("/nuevo")
    public String nuevaVentaClienteHabitacionForm(Model modelo, RedirectAttributes redirectAttributes) {
        VentasClientesHabitacion ventaClienteHabitacion = new VentasClientesHabitacion();
        List<Ventas> ventas = obtenerVentas();
        List<Usuarios> usuarios_cliente = obtenerUsuarios()
                .stream()
                .filter(usuario_cliente -> usuario_cliente.getRol().getNivel() == 0)
                .collect(Collectors.toList());

        if (ventas.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay VENTAS necesarias para crear un nuevo registro aquí.");

            return REDIRECT_LISTAR;
        }

        if (usuarios_cliente.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay USUARIOS CLIENTES necesarios para crear un nuevo registro aquí.");

            return REDIRECT_LISTAR;
        }

        modelo.addAttribute("ventas", ventas);
        modelo.addAttribute("usuarios_cliente", usuarios_cliente);
        modelo.addAttribute("ventasClientesHabitacion", obtenerVentasClientesHabitacion());
        modelo.addAttribute("ventaClienteHabitacion", ventaClienteHabitacion);

        return VIEW_NUEVO;
    }

    @PostMapping
    public String crearVentaClienteHabitacion(
            @ModelAttribute("ventaClienteHabitacion") VentasClientesHabitacion ventaClienteHabitacion) {
        servicio.createVentaClienteHabitacion(ventaClienteHabitacion);

        return REDIRECT_LISTAR;
    }

    @GetMapping("/editar/{id}")
    public String editarVentaClienteHabitacionForm(@PathVariable Long id, Model modelo,
            RedirectAttributes redirectAttributes) {
        List<Ventas> ventas = obtenerVentas();
        List<Usuarios> usuarios_cliente = obtenerUsuarios()
                .stream()
                .filter(usuario_cliente -> usuario_cliente.getRol().getNivel() == 0)
                .collect(Collectors.toList());

        if (usuarios_cliente.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No hay USUARIOS CLIENTES necesarios para crear un nuevo registro aquí.");

            return REDIRECT_LISTAR;
        }

        modelo.addAttribute("ventas", ventas);
        modelo.addAttribute("usuarios_cliente", usuarios_cliente);
        modelo.addAttribute("ventasClientesHabitacion", obtenerVentasClientesHabitacion());
        modelo.addAttribute("ventaClienteHabitacion", servicio.getVentaClienteHabitacionById(id));

        return VIEW_EDITAR;
    }

    @PostMapping("/{id}")
    public String actualizarVentaClienteHabitacion(@PathVariable Long id,
            @ModelAttribute("ventaClienteHabitacion") VentasClientesHabitacion ventaClienteHabitacion, Model modelo) {
        VentasClientesHabitacion ventaClienteHabitacionExistente = servicio.getVentaClienteHabitacionById(id);

        ventaClienteHabitacionExistente.setUsuario_alojado(ventaClienteHabitacion.getUsuario_alojado());
        ventaClienteHabitacionExistente.setVenta(ventaClienteHabitacion.getVenta());

        ventaClienteHabitacionExistente
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        servicio.updateVentaClienteHabitacion(ventaClienteHabitacionExistente);

        return REDIRECT_LISTAR;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarVentaClienteHabitacion(@PathVariable Long id) {
        VentasClientesHabitacion ventaClienteHabitacionExistente = servicio.getVentaClienteHabitacionById(id);

        ventaClienteHabitacionExistente.setEliminado(true);

        servicio.updateVentaClienteHabitacion(ventaClienteHabitacionExistente);

        return REDIRECT_LISTAR;
    }

    private List<VentasClientesHabitacion> obtenerVentasClientesHabitacion() {
        return servicio.getVentasClientesHabitacion()
                .stream()
                .filter(ventaClienteHabitacion -> !ventaClienteHabitacion.isEliminado())
                .sorted(Comparator.comparing(VentasClientesHabitacion::getId_venta_cliente_habitacion).reversed())
                .collect(Collectors.toList());
    }

    private List<Ventas> obtenerVentas() {
        return ventasServicio.getVentas()
                .stream()
                .filter(venta -> !venta.isEliminado())
                .collect(Collectors.toList());
    }

    private List<Usuarios> obtenerUsuarios() {
        return usuariosServicio.getUsuarios()
                .stream()
                .filter(usuario -> !usuario.isEliminado())
                .collect(Collectors.toList());
    }
}
