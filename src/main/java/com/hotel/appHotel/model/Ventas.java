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
@Table(name = "ventas")
public class Ventas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_venta;

    @ManyToOne
    @JoinColumn(name = "usuario_responsable", referencedColumnName = "id_usuario")
    private Usuarios usuario_responsable;

    @ManyToOne
    @JoinColumn(name = "habitacion_precio", referencedColumnName = "id_habitacion_precio")
    private HabitacionesPrecio habitacion_precio;

    @Column(name = "fecha_entrada")
    private String fecha_entrada; // Opcional para cotizaciones

    @Column(name = "fecha_salida")
    private String fecha_salida;

    @Column(name = "descuento", nullable = false)
    private Double descuento;

    @Column(name = "monto_total", nullable = false)
    private Double monto_total;

    @Column(name = "tipo_venta", nullable = false)
    private String tipo_venta; // "Cotización", "Alquiler", "Venta"

    @Column(name = "fecha_creacion", nullable = false)
    private String fecha_creacion; // ISO 8601

    @PrePersist
    private void prePersist() {
        this.fecha_creacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
