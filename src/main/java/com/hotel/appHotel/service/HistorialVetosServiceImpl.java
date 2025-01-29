package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.appHotel.model.HistorialVetos;
import com.hotel.appHotel.repository.HistorialVetosRepository;

@Service
public class HistorialVetosServiceImpl implements HistorialVetosService {
    @Autowired
    HistorialVetosRepository repositorio;

    @Override
    public List<HistorialVetos> getHistorialVetos() {
        return repositorio.findAll();
    }

    @Override
    public HistorialVetos createHistorialVeto(HistorialVetos historialVeto) {
        return repositorio.save(historialVeto);
    }

    @Override
    public HistorialVetos getHistorialVetoById(Long id) {
        return repositorio.findById(id).get();
    }

    @Override
    public HistorialVetos updateHistorialVeto(HistorialVetos historialVeto) {
        return repositorio.save(historialVeto);
    }

    @Override
    public void deleteHistorialVeto(Long id) {
        repositorio.deleteById(id);
    }
}
