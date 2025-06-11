package com.springboot.backend.andres.usersapp.usersbackend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.springboot.backend.andres.usersapp.usersbackend.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {


    Optional<Role> findByName(String name);

    

}
