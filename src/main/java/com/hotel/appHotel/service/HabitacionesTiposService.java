package com.hotel.appHotel.service;

import java.util.List;

import com.hotel.appHotel.model.HabitacionesTipos;

public interface HabitacionesTiposService {
    public List<HabitacionesTipos> getHabitacionesTipos();

    public HabitacionesTipos createHabitacionTipo(HabitacionesTipos habitaccreateHabitacionesTipos);

    public HabitacionesTipos getHabitacionTipoById(Long id);

    public HabitacionesTipos updateHabitacionTipo(HabitacionesTipos habitaccreateHabitacionesTipos);

    public void deleteHabitacionTipo(Long id);
}
