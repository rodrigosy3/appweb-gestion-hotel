package com.hotel.appHotel.service;

import java.util.List;

import com.hotel.appHotel.model.HabitacionesEstado;

public interface HabitacionesEstadoService {
    public List<HabitacionesEstado> getHabitacionesEstado();

    public HabitacionesEstado createHabitacionEstado(HabitacionesEstado habitacionEstado);

    public HabitacionesEstado getHabitacionEstadoById(Long id);

    public HabitacionesEstado updateHabitacionEstado(HabitacionesEstado habitacionEstado);

    public void deleteHabitacionEstado(Long id);

    public HabitacionesEstado getByEstado(String estado);
}
