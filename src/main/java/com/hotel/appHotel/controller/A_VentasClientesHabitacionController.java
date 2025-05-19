package com.hotel.appHotel.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.model.Ventas;
import com.hotel.appHotel.model.VentasClientesHabitacion;
import com.hotel.appHotel.service.UsuariosService;
import com.hotel.appHotel.service.VentasClientesHabitacionService;
import com.hotel.appHotel.service.VentasService;

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
    public String listarVentasClientesHabitacionPorPagina(@RequestParam Map<String, Object> params, Model model) {
        int page = params.get("page") != null ? (Integer.parseInt(params.get("page").toString()) - 1) : 0;

        PageRequest pageRequest = PageRequest.of(page, 20);

        Page<VentasClientesHabitacion> pageEntidad = servicio.getVentasClientesHabitacionNoEliminados(pageRequest);

        int totalPages = pageEntidad.getTotalPages();

        if (totalPages > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }

        model.addAttribute("ventasClientesHabitacion", pageEntidad.getContent());
        model.addAttribute("actualPage", page + 1);
        model.addAttribute("nextPage", page + 2);
        model.addAttribute("prevPage", page);
        model.addAttribute("lastPage", totalPages);

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
                .setFecha_creacion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

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

    private List<Ventas> obtenerVentas() {
        return ventasServicio.getVentas()
                .stream()
                .filter(venta -> !venta.isEliminado())
                .sorted(Comparator.comparing(Ventas::getId_venta).reversed())
                .collect(Collectors.toList());
    }

    private List<Usuarios> obtenerUsuarios() {
        return usuariosServicio.getUsuarios()
                .stream()
                .filter(usuario -> !usuario.isEliminado())
                .sorted(Comparator.comparing(Usuarios::getId_usuario).reversed())
                .collect(Collectors.toList());
    }
}
