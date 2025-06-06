package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hotel.appHotel.model.Cajas;

public interface CajasService {

    public List<Cajas> getCajas();

    public Cajas createCaja(Cajas caja);

    public Cajas getCajaById(Long id);

    public Cajas updateCaja(Cajas caja);

    public void deleteCaja(Long id);

    public List<Cajas> getCajasDelDia();

    public List<Cajas> getCajasPorVenta(Long idVenta);

    Page<Cajas> getCajasNoEliminadas(Pageable pageable);

}
