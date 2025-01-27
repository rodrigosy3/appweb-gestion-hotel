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
@Table(name = "ventas_clientes_habitacion")
public class VentasClientesHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_usuario_habitacion;

    @ManyToOne
    @JoinColumn(name = "usuario_alojado", referencedColumnName = "id_usuario")
    private Usuarios usuario_alojado;

    @ManyToOne
    @JoinColumn(name = "venta", referencedColumnName = "id_venta")
    private Ventas venta;

    @Column(name = "fecha_creacion", nullable = false)
    private String fecha_creacion; // ISO 8601

    @PrePersist
    private void prePersist() {
        this.fecha_creacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
