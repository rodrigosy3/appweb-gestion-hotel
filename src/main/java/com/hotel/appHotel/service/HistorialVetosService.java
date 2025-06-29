package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hotel.appHotel.model.HistorialVetos;

public interface HistorialVetosService {
    public List<HistorialVetos> getHistorialVetos();

    public HistorialVetos createHistorialVeto(HistorialVetos historialVeto);

    public HistorialVetos getHistorialVetoById(Long id);

    public HistorialVetos updateHistorialVeto(HistorialVetos historialVeto);

    public void deleteHistorialVeto(Long id);

    Page<HistorialVetos> getHistorialVetosNoEliminados(Pageable pageable);
}
