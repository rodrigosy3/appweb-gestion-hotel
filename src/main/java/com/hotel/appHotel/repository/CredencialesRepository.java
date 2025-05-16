package com.hotel.appHotel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.Credenciales;

@Repository
public interface CredencialesRepository extends JpaRepository<Credenciales, Long> {

    @Query("SELECT c FROM Credenciales c WHERE c.usuario.dni = :dni AND c.eliminado = false")
    Optional<Credenciales> buscarPorDni(@Param("dni") String dni);

}
