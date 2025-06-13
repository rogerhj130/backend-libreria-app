package com.springboot.backend.andres.usersapp.usersbackend.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.springboot.backend.andres.usersapp.usersbackend.entities.Visita;
@Repository
public interface VisitaRepository extends JpaRepository<Visita, Long> {
}
