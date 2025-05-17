package com.hotel.appHotel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.HistorialVetos;

@Repository
public interface HistorialVetosRepository extends JpaRepository<HistorialVetos, Long> {

    @Query("SELECT hv FROM HistorialVetos hv WHERE hv.eliminado = false ORDER BY hv.id_historial_veto DESC")
    Page<HistorialVetos> findByEliminadoFalseDesc(Pageable pageable);

}
