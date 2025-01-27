package com.hotel.appHotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.Roles;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {

}
