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
@Table(name = "habitaciones_tipos")
public class HabitacionesTipos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_habitacion_tipo;

    @Column(name = "nombre_tipo")
    private String nombre_tipo;

    @Column(name = "abreviacion_tipo")
    private String abreviacion_tipo;

    @Column(name = "fecha_creacion")
    private String fecha_creacion; // ISO 8601

    @Column(name = "eliminado")
    private boolean eliminado = false;

    // Genera la fecha en el momento del guardado y no cuando se empezó a crear la entidad
    @PrePersist
    private void prePersist() {
        this.fecha_creacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }
}
