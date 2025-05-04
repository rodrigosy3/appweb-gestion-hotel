package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.appHotel.model.HabitacionesCaracteristicas;
import com.hotel.appHotel.repository.HabitacionesCaracteristicasRepository;

@Service
public class HabitacionesCaracteristicasServiceImpl implements HabitacionesCaracteristicasService {

    @Autowired
    private HabitacionesCaracteristicasRepository repositorio;

    @Override
    public List<HabitacionesCaracteristicas> getHabitacionesCaracteristica() {
        return repositorio.findAll();
    }

    @Override
    @Transactional
    public HabitacionesCaracteristicas createHabitacionCaracteristica(HabitacionesCaracteristicas habitacionCaracteristica) {
        return repositorio.save(habitacionCaracteristica);
    }

    @Override
    public HabitacionesCaracteristicas getHabitacionCaracteristicaById(Long id) {
        return repositorio.findById(id).get();
    }

    @Override
    @Transactional
    public HabitacionesCaracteristicas updateHabitacionCaracteristica(HabitacionesCaracteristicas habitacionCaracteristica) {
        return repositorio.save(habitacionCaracteristica);
    }

    @Override
    @Transactional
    public void deleteHabitacionCaracteristica(Long id) {
        repositorio.deleteById(id);
    }
}
