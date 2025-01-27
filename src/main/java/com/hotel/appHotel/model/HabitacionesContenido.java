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
@Table(name = "habitaciones_contenido")
public class HabitacionesContenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_habitaciones_contenido;

    @ManyToOne
    @JoinColumn(name = "habitacion", referencedColumnName = "id_habitacion")
    private Habitaciones habitacion;

    @ManyToOne
    @JoinColumn(name = "caracteristica", referencedColumnName = "id_caracteristica_habitacion")
    private HabitacionesCaracteristicas caracteristica;

    @Column(name = "estado_caracteristica", nullable = false)
    private String estado_caracteristica; // "Disponible", "Mantenimiento", etc.

    @Column(name = "razon_estado", nullable = false)
    private String razon_estado;

    @Column(name = "fecha_creacion")
    private String fecha_creacion; // ISO 8601

    @PrePersist
    private void prePersist() {
        this.fecha_creacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
