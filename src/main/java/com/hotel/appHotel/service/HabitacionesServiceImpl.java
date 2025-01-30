package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.appHotel.model.Habitaciones;
import com.hotel.appHotel.repository.HabitacionesRepository;

@Service
public class HabitacionesServiceImpl implements HabitacionesService {

    @Autowired
    private HabitacionesRepository repositorio;

    @Override
    public List<Habitaciones> getHabitaciones() {
        return repositorio.findAll();
    }

    @Override
    public Habitaciones createHabitacion(Habitaciones habitacion) {
        return repositorio.save(habitacion);
    }

    @Override
    public Habitaciones getHabitacionById(Long id) {
        return repositorio.findById(id).get();
    }

    @Override
    public Habitaciones updateHabitacion(Habitaciones habitacion) {
        return repositorio.save(habitacion);
    }

    @Override
    public void deleteHabitacion(Long id) {
        repositorio.deleteById(id);
    }
}
