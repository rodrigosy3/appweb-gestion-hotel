package com.hotel.appHotel.service;

import java.util.List;

import com.hotel.appHotel.model.VentasClientesHabitacion;

public interface VentasClientesHabitacionService {
    public List<VentasClientesHabitacion> getVentasClientesHabitacion();

    public VentasClientesHabitacion createVentaClienteHabitacion(VentasClientesHabitacion ventaClienteHabitacion);

    public VentasClientesHabitacion getVentaClienteHabitacionById(Long id);

    public VentasClientesHabitacion updateVentaClienteHabitacion(VentasClientesHabitacion ventaClienteHabitacion);

    public void deleteVentaClienteHabitacionById(Long id);

    public VentasClientesHabitacion getByVentaCliente(Long id_venta, Long id_usuario);

    public void deleteByVentaCliente(Long id_venta, Long id_usuario);
}
