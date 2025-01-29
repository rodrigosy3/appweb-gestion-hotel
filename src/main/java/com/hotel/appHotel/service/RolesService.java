package com.hotel.appHotel.service;

import java.util.List;

import com.hotel.appHotel.model.Roles;

public interface RolesService {
    public List<Roles> getRoles();

    public Roles createRol(Roles rol);

    public Roles getRolById(Long id);

    public Roles updateRol(Roles rol);

    public void deleteRol(Long id);

    public Roles getByName(String nombre);
}
