package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.appHotel.model.HabitacionesContenido;
import com.hotel.appHotel.repository.HabitacionesContenidoRepository;

@Service
public class HabitacionesContenidoServiceImpl implements HabitacionesContenidoService {

    @Autowired
    private HabitacionesContenidoRepository repositorio;

    @Override
    public List<HabitacionesContenido> getHabitacionesContenido() {
        return repositorio.findAll();
    }

    @Override
    @Transactional
    public HabitacionesContenido createHabitacionContenido(HabitacionesContenido habitacionContenido) {
        return repositorio.save(habitacionContenido);
    }

    @Override
    public HabitacionesContenido getHabitacionContenidoById(Long id) {
        return repositorio.findById(id).get();
    }

    @Override
    @Transactional
    public HabitacionesContenido updateHabitacionContenido(HabitacionesContenido habitacionContenido) {
        return repositorio.save(habitacionContenido);
    }

    @Override
    @Transactional
    public void deleteHabitacionContenido(Long id) {
        repositorio.deleteById(id);
    }

    @Override
    public Page<HabitacionesContenido> getHabitacionesContenidoNoEliminados(Pageable pageable) {
        return repositorio.findByEliminadoFalseDesc(pageable);
    }
}
