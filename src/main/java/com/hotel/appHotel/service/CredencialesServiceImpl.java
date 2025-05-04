package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.appHotel.model.Credenciales;
import com.hotel.appHotel.repository.CredencialesRepository;

@Service
public class CredencialesServiceImpl implements CredencialesService {
    @Autowired
    CredencialesRepository repositorio;

    @Override
    public List<Credenciales> getCredenciales() {
        return repositorio.findAll();
    }

    @Override
    @Transactional
    public Credenciales createCredencial(Credenciales credencial) {
        return repositorio.save(credencial);
    }

    @Override
    public Credenciales getCredencialById(Long id) {
        return repositorio.findById(id).get();
    }

    @Override
    @Transactional
    public Credenciales updateCredencial(Credenciales credencial) {
        return repositorio.save(credencial);
    }

    @Override
    @Transactional
    public void deleteCredencial(Long id) {
        repositorio.deleteById(id);
    }
}
