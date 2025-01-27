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
@Table(name = "habitaciones_precio")
public class HabitacionesPrecio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_habitacion_precio;

    @ManyToOne
    @JoinColumn(name = "habitacion", referencedColumnName = "id_habitacion")
    private Habitaciones habitacion;

    @Column(name = "tiempo_estadia", nullable = false)
    private Integer tiempo_estadia; // En horas

    @Column(name = "precio", nullable = false)
    private Double precio;

    @Column(name = "fecha_creacion", nullable = false)
    private String fecha_creacion; // ISO 8601

    @PrePersist
    private void prePersist() {
        this.fecha_creacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
