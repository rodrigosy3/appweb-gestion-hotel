package com.hotel.appHotel.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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
    private Long id_habitacion_contenido;

    @ManyToOne
    @JoinColumn(name = "habitacion", referencedColumnName = "id_habitacion")
    private Habitaciones habitacion;

    @ManyToOne
    @JoinColumn(name = "caracteristica", referencedColumnName = "id_habitacion_caracteristica")
    private HabitacionesCaracteristicas caracteristica;

    @Column(name = "estado_caracteristica")
    private String estado_caracteristica; // "Disponible", "Mantenimiento", etc.

    @Column(name = "razon_estado")
    private String razon_estado;

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
