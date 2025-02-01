package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.appHotel.model.VentasClientesHabitacion;
import com.hotel.appHotel.repository.VentasClientesHabitacionRepository;

@Service
public class VentasClientesHabitacionServiceImpl implements VentasClientesHabitacionService {

    @Autowired
    private VentasClientesHabitacionRepository repositorio;

    @Override
    public List<VentasClientesHabitacion> getVentasClientesHabitacion() {
        return repositorio.findAll();
    }

    @Override
    public VentasClientesHabitacion createVentaClienteHabitacion(VentasClientesHabitacion ventaClienteHabitacion) {
        return repositorio.save(ventaClienteHabitacion);
    }

    @Override
    public VentasClientesHabitacion getVentaClienteHabitacionById(Long id) {
        return repositorio.findById(id).get();
    }

    @Override
    public VentasClientesHabitacion updateVentaClienteHabitacion(VentasClientesHabitacion ventaClienteHabitacion) {
        return repositorio.save(ventaClienteHabitacion);
    }

    @Override
    public void deleteVentaClienteHabitacion(Long id) {
        repositorio.deleteById(id);
    }
}
