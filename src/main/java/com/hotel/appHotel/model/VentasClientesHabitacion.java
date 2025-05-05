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
@Table(name = "ventas_clientes_habitacion")
public class VentasClientesHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_venta_cliente_habitacion;

    @ManyToOne
    @JoinColumn(name = "usuario_alojado", referencedColumnName = "id_usuario")
    private Usuarios usuario_alojado;

    @ManyToOne
    @JoinColumn(name = "venta", referencedColumnName = "id_venta")
    private Ventas venta;

    @Column(name = "fecha_creacion", nullable = false)
    private String fecha_creacion; // ISO 8601
    
    @Column(name = "eliminado")
    private boolean eliminado = false;

    // Genera la fecha en el momento del guardado y no cuando se empezó a crear la entidad
    @PrePersist
    private void prePersist() {
        this.fecha_creacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }
}
