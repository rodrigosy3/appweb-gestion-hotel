package com.hotel.appHotel.service;

import java.util.List;

import com.hotel.appHotel.model.HabitacionesPrecio;

public interface HabitacionesPrecioService {
    public List<HabitacionesPrecio> getHabitacionesPrecio();

    public HabitacionesPrecio createHabitacionPrecio(HabitacionesPrecio habitacionPrecio);

    public HabitacionesPrecio getHabitacionPrecioById(Long id);

    public HabitacionesPrecio updateHabitacionPrecio(HabitacionesPrecio habitacionPrecio);

    public void deleteHabitacionPrecio(Long id);
}
