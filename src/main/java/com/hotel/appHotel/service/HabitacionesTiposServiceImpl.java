package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.appHotel.model.HabitacionesTipos;
import com.hotel.appHotel.repository.HabitacionesTiposRepository;

@Service
public class HabitacionesTiposServiceImpl implements HabitacionesTiposService {

    @Autowired
    private HabitacionesTiposRepository repositorio;

    @Override
    public List<HabitacionesTipos> getHabitacionesTipos() {
        return repositorio.findAll();
    }

    @Override
    public HabitacionesTipos createHabitacionTipo(HabitacionesTipos habitacionTipo) {
        return repositorio.save(habitacionTipo);
    }

    @Override
    public HabitacionesTipos getHabitacionTipoById(Long id) {
        return repositorio.findById(id).get();
    }

    @Override
    public HabitacionesTipos updateHabitacionTipo(HabitacionesTipos habitacionTipo) {
        return repositorio.save(habitacionTipo);
    }

    @Override
    public void deleteHabitacionTipo(Long id) {
        repositorio.deleteById(id);
    }
}
