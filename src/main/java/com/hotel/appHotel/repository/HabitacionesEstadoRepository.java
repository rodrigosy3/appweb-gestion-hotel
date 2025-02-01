package com.hotel.appHotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.HabitacionesEstado;

@Repository
public interface HabitacionesEstadoRepository extends JpaRepository<HabitacionesEstado, Long> {
    HabitacionesEstado findByEstado(String estado);
}
