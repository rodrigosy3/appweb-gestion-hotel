package com.hotel.appHotel.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "habitaciones_estado")
public class HabitacionesEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_habitaciones_estado;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "mensaje")
    private String mensaje;

    @Column(name = "fecha_creacion", nullable = false)
    private String fecha_creacion;

    @PrePersist
    private void prePersist() {
        this.fecha_creacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
