package com.hotel.appHotel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.HabitacionesContenido;

@Repository
public interface HabitacionesContenidoRepository extends JpaRepository<HabitacionesContenido, Long> {

    @Query("SELECT hc FROM HabitacionesContenido hc WHERE hc.eliminado = false ORDER BY hc.id_habitacion_contenido DESC")
    Page<HabitacionesContenido> findByEliminadoFalseDesc(Pageable pageable);

}
