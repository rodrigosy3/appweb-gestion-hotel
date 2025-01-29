package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.appHotel.model.Roles;
import com.hotel.appHotel.repository.RolesRepository;

@Service
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesRepository repositorio;

    @Override
    public List<Roles> getRoles() {
        return repositorio.findAll();
    }

    @Override
    public Roles createRol(Roles rol) {
        return repositorio.save(rol);
    }

    @Override
    public Roles getRolById(Long id) {
        return repositorio.findById(id).get();
    }

    @Override
    public Roles updateRol(Roles rol) {
        return repositorio.save(rol);
    }

    @Override
    public void deleteRol(Long id) {
        repositorio.deleteById(id);
    }

    @Override
    public Roles getByName(String nombre) {
        return repositorio.findByNombre(nombre);
    }
}
