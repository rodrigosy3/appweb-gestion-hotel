package com.hotel.appHotel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "cajas")
public class Cajas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idCaja;

    @ManyToOne
    @JoinColumn(name = "usuario_responsable", referencedColumnName = "id_usuario")
    private Usuarios usuarioResponsable;

    @Column(name = "monto")
    private Double monto;

    @Column(name = "fecha_registro")
    private String fechaRegistro;

    @ManyToOne
    @JoinColumn(name = "venta", referencedColumnName = "id_venta")
    private Ventas venta;

    @Column(name = "eliminado")
    private Boolean eliminado = false;
}
