package com.springboot.backend.andres.usersapp.usersbackend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_ajustes")
public class HistorialAjuste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long almacenOrigenId;

    private Long almacenDestinoId;

    private Long medicamentoId;

    private Integer cantidad;

    private String motivo;

    private LocalDateTime fecha;

        private String estado;

    public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public Long getAlmacenOrigenId() {
        return almacenOrigenId;
    }

    public void setAlmacenOrigenId(Long almacenOrigenId) {
        this.almacenOrigenId = almacenOrigenId;
    }

    public Long getAlmacenDestinoId() {
        return almacenDestinoId;
    }

    public void setAlmacenDestinoId(Long almacenDestinoId) {
        this.almacenDestinoId = almacenDestinoId;
    }

    public Long getMedicamentoId() {
        return medicamentoId;
    }

    public void setMedicamentoId(Long medicamentoId) {
        this.medicamentoId = medicamentoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}