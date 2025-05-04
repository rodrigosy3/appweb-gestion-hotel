package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.repository.UsuariosRepository;

@Service
public class UsuariosServiceImpl implements UsuariosService {
    @Autowired
    UsuariosRepository repositorio;

    @Override
    public List<Usuarios> getUsuarios() {
        return repositorio.findAll();
    }

    @Override
    @Transactional
    public Usuarios createUsuario(Usuarios usuario) {
        return repositorio.save(usuario);
    }

    @Override
    public Usuarios getUsuarioById(Long id) {
        return repositorio.findById(id).get();
    }

    @Override
    @Transactional
    public Usuarios updateUsuario(Usuarios usuario) {
        return repositorio.save(usuario);
    }

    @Override
    @Transactional
    public void deleteUsuario(Long id) {
        repositorio.deleteById(id);
    }

    @Override
    public List<Usuarios> getUsuariosExcluyendoRol(String rol) {
        return repositorio.findByRol_NombreNot(rol);
    } 

    @Override
    public Usuarios getUsuarioByDni(String dni) {
        return repositorio.findByDni(dni);
    }
}
