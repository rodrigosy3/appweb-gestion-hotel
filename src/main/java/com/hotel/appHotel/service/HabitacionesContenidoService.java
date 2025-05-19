package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hotel.appHotel.model.HabitacionesContenido;

public interface HabitacionesContenidoService {
    public List<HabitacionesContenido> getHabitacionesContenido();

    public HabitacionesContenido createHabitacionContenido(HabitacionesContenido habitacionContenido);

    public HabitacionesContenido getHabitacionContenidoById(Long id);

    public HabitacionesContenido updateHabitacionContenido(HabitacionesContenido habitacionContenido);

    public void deleteHabitacionContenido(Long id);

    Page<HabitacionesContenido> getHabitacionesContenidoNoEliminados(Pageable pageable);
}
