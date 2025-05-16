package com.hotel.appHotel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.Credenciales;

@Repository
public interface CredencialesRepository extends JpaRepository<Credenciales, Long> {

    @Query(value = "SELECT c.* FROM credenciales c INNER JOIN usuarios u ON c.usuario = u.id_usuario WHERE u.dni = :dni AND c.eliminado = false", nativeQuery = true)
    Optional<Credenciales> buscarPorDni(@Param("dni") String dni);

}
