package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.appHotel.model.HabitacionesPrecio;
import com.hotel.appHotel.repository.HabitacionesPrecioRepository;

@Service
public class HabitacionesPrecioServiceImpl implements HabitacionesPrecioService {

    @Autowired
    private HabitacionesPrecioRepository repositorio;

    @Override
    public List<HabitacionesPrecio> getHabitacionesPrecio() {
        return repositorio.findAll();
    }

    @Override
    public HabitacionesPrecio createHabitacionPrecio(HabitacionesPrecio habitacionPrecio) {
        return repositorio.save(habitacionPrecio);
    }

    @Override
    public HabitacionesPrecio getHabitacionPrecioById(Long id) {
        return repositorio.findById(id).get();
    }

    @Override
    public HabitacionesPrecio updateHabitacionPrecio(HabitacionesPrecio habitacionPrecio) {
        return repositorio.save(habitacionPrecio);
    }

    @Override
    public void deleteHabitacionPrecio(Long id) {
        repositorio.deleteById(id);
    }
}
