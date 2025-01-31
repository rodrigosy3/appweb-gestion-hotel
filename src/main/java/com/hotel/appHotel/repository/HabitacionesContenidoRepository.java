package com.hotel.appHotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.HabitacionesContenido;

@Repository
public interface HabitacionesContenidoRepository extends JpaRepository<HabitacionesContenido, Long> {
}
