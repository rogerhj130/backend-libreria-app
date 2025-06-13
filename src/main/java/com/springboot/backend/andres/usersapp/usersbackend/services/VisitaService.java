package com.springboot.backend.andres.usersapp.usersbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.backend.andres.usersapp.usersbackend.entities.Visita;
import com.springboot.backend.andres.usersapp.usersbackend.repositories.VisitaRepository;

@Service
public class VisitaService {

    @Autowired
    private VisitaRepository visitaRepository;

    public void registrarVisita() {
        visitaRepository.save(new Visita());
    }

    public long contarVisitas() {
        return visitaRepository.count();
    }
}