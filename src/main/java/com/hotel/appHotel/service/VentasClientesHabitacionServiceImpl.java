package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public VentasClientesHabitacion createVentaClienteHabitacion(VentasClientesHabitacion ventaClienteHabitacion) {
        return repositorio.save(ventaClienteHabitacion);
    }

    @Override
    public VentasClientesHabitacion getVentaClienteHabitacionById(Long id) {
        return repositorio.findById(id).get();
    }

    @Override
    @Transactional
    public VentasClientesHabitacion updateVentaClienteHabitacion(VentasClientesHabitacion ventaClienteHabitacion) {
        return repositorio.save(ventaClienteHabitacion);
    }

    @Override
    @Transactional
    public void deleteVentaClienteHabitacionById(Long id) {
        repositorio.deleteById(id);
    }

    @Override
    public VentasClientesHabitacion getByVentaCliente(Long id_venta, Long id_usuario) {
        return repositorio.findByVentaIdAndUsuarioId(id_venta, id_usuario);
    }

    @Override
    @Transactional
    public void deleteByVentaCliente(Long id_venta, Long id_usuario) {
        repositorio.deleteByVentaIdAndUsuarioId(id_venta, id_usuario);
    }
}
