package com.hotel.appHotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.VentasClientesHabitacion;

import jakarta.transaction.Transactional;

@Repository
public interface VentasClientesHabitacionRepository extends JpaRepository<VentasClientesHabitacion, Long> {

    @Query("SELECT v FROM VentasClientesHabitacion v WHERE v.venta.id_venta = :idVenta AND v.usuario_alojado.id_usuario = :idUsuario")
    VentasClientesHabitacion findByVentaIdAndUsuarioId(@Param("idVenta") Long idVenta,
            @Param("idUsuario") Long idUsuario);

    @Modifying
    @Transactional
    @Query("DELETE FROM VentasClientesHabitacion v WHERE v.venta.id_venta = :idVenta AND v.usuario_alojado.id_usuario = :idUsuario")
    void deleteByVentaIdAndUsuarioId(@Param("idVenta") Long idVenta, @Param("idUsuario") Long idUsuario);
}
