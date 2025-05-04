package com.hotel.appHotel.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteDTO {
    private String dni;
    private String nombres;
    private String apellidos;
    private Integer edad;
    private String celular;
    private boolean eliminado;
}