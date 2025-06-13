package com.springboot.backend.andres.usersapp.usersbackend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.backend.andres.usersapp.usersbackend.services.VisitaService;




@RestController
@RequestMapping("/api/visitas")
//@CrossOrigin(origins = "*") // Ajusta si usas un frontend en Vercel
public class VisitaController {

    @Autowired
    private VisitaService visitaService;

    @PostMapping
    public ResponseEntity<?> registrarVisita() {
        visitaService.registrarVisita();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> obtenerCantidadVisitas() {
        return ResponseEntity.ok(visitaService.contarVisitas());
    }
}