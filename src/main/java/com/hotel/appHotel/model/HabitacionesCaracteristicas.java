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
@Table(name = "habitaciones_caracteristicas")
public class HabitacionesCaracteristicas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_habitacion_caracteristica;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "marca")
    private String marca;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio")
    private Double precio = 0.0;

    @Column(name = "fecha_creacion", nullable = false)
    private String fecha_creacion; // ISO 8601

    @PrePersist
    private void prePersist() {
        this.fecha_creacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}