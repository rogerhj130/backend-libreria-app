package com.springboot.backend.andres.usersapp.usersbackend.dto;

public class TraspasoRequest {
    private Long medicamentoId;
    private Integer cantidad;

     private String motivo;
     private String estado; // "INGRESO" o "EGRESO"

     private Long almacenOrigenId;  // ultimo aumento para que el traspaso sea dinamico, que el usuario indique los almacenes
     private Long almacenDestinoId; 



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

    public String getMotivo() {
        return motivo;
    }

     public void setMotivo(String motivo) {
         this.motivo = motivo;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
