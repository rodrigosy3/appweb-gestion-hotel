package com.hotel.appHotel.service;

import java.util.List;

import com.hotel.appHotel.model.HabitacionesCaracteristicas;

public interface HabitacionesCaracteristicasService {
    public List<HabitacionesCaracteristicas> getHabitacionesCaracteristicas();

    public HabitacionesCaracteristicas createHabitacionCaracteristica(HabitacionesCaracteristicas habitacionCaracteristica);

    public HabitacionesCaracteristicas getHabitacionCaracteristicaById(Long id);

    public HabitacionesCaracteristicas updateHabitacionCaracteristica(HabitacionesCaracteristicas habitacionCaracteristica);

    public void deleteHabitacionCaracteristica(Long id);
}
