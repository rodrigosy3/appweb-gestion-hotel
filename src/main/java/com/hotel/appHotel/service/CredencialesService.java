package com.hotel.appHotel.service;

import java.util.List;

import com.hotel.appHotel.model.Credenciales;

public interface CredencialesService {
    public List<Credenciales> getCredenciales();

    public Credenciales createCredencial(Credenciales credencial);

    public Credenciales getCredencialById(Long id);

    public Credenciales updateCredencial(Credenciales credencial);

    public void deleteCredencial(Long id);
}
