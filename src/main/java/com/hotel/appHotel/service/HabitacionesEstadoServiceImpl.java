package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.appHotel.model.HabitacionesEstado;
import com.hotel.appHotel.repository.HabitacionesEstadoRepository;

@Service
public class HabitacionesEstadoServiceImpl implements HabitacionesEstadoService {

    @Autowired
    private HabitacionesEstadoRepository repositorio;

    @Override
    public List<HabitacionesEstado> getHabitacionesEstado() {
        return repositorio.findAll();
    }

    @Override
    public HabitacionesEstado createHabitacionEstado(HabitacionesEstado habitacionEstado) {
        return repositorio.save(habitacionEstado);
    }

    @Override
    public HabitacionesEstado getHabitacionEstadoById(Long id) {
        return repositorio.findById(id).get();
    }

    @Override
    public HabitacionesEstado updateHabitacionEstado(HabitacionesEstado habitacionEstado) {
        return repositorio.save(habitacionEstado);
    }

    @Override
    public void deleteHabitacionEstado(Long id) {
        repositorio.deleteById(id);
    }

    @Override
    public HabitacionesEstado getByEstado(String estado) {
        return repositorio.findByEstado(estado);
    }
}
