package com.hotel.appHotel.service;

import java.util.List;

import com.hotel.appHotel.model.Usuarios;

public interface UsuariosService {
    public List<Usuarios> getUsuarios();

    public Usuarios createUsuario(Usuarios usuario);

    public Usuarios getUsuarioById(Long id);

    public Usuarios updateUsuario(Usuarios usuario);

    public void deleteUsuario(Long id);

    public List<Usuarios> getUsuariosExcluyendoRol(String rol);

    public Usuarios getUsuarioByDni(String dni);
}
