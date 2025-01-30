package com.hotel.appHotel.service;

import java.util.List;

import com.hotel.appHotel.model.Habitaciones;

public interface HabitacionesService {
    public List<Habitaciones> getHabitaciones();

    public Habitaciones createHabitacion(Habitaciones habitacion);

    public Habitaciones getHabitacionById(Long id);

    public Habitaciones updateHabitacion(Habitaciones habitacion);

    public void deleteHabitacion(Long id);
}
