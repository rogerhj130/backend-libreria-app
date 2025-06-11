package com.springboot.backend.andres.usersapp.usersbackend.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.andres.usersapp.usersbackend.dto.AlmacenMedicamentosDTO;
import com.springboot.backend.andres.usersapp.usersbackend.dto.TraspasoRequest;
import com.springboot.backend.andres.usersapp.usersbackend.entities.Almacen;
import com.springboot.backend.andres.usersapp.usersbackend.entities.DetalleAlmacen;
import com.springboot.backend.andres.usersapp.usersbackend.entities.DetalleAlmacenId;
import com.springboot.backend.andres.usersapp.usersbackend.entities.HistorialAjuste;
import com.springboot.backend.andres.usersapp.usersbackend.entities.Medicamento;
import com.springboot.backend.andres.usersapp.usersbackend.repositories.AlmacenRepository;
import com.springboot.backend.andres.usersapp.usersbackend.repositories.DetalleAlmacenRepository;
import com.springboot.backend.andres.usersapp.usersbackend.repositories.HistorialAjusteRepository;

@Service
public class AlmacenServiceImp implements AlmacenService {

    @Autowired
    private AlmacenRepository almacenRepository;

    @Autowired
    private DetalleAlmacenRepository detalleAlmacenRepository;

     @Autowired
    private HistorialAjusteRepository historialAjusteRepository; // ✅ aquí

    @Transactional(readOnly = true)
    @Override
    public List<Almacen> listarTodo() {
        return (List<Almacen>) almacenRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Almacen> paginarTodo(Pageable pageable) {
        return this.almacenRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Almacen> buscarPorId(Long id) {
        return almacenRepository.findById(id);
    }

    @Transactional
    @Override
    public Almacen guardar(Almacen almacen) {
        if (existeElAlmacen(almacen.getNombre())) {
            throw new RuntimeException(
                    "El almacen con nombre: " + almacen.getNombre() + " ya existe en la base de datos");
        }
        return almacenRepository.save(almacen);
    }

    @Transactional
    @Override
    public Optional<Almacen> actualizar(Long id, Almacen almacen) {
        Optional<Almacen> productOptional = almacenRepository.findById(id);
        if (productOptional.isPresent()) {
            Almacen almacenDB = productOptional.orElseThrow();

            // Actualizar todos los campos
            almacenDB.setNombre(almacen.getNombre());
            almacenDB.setDireccion(almacen.getDireccion());

            // Guardar los cambios en la base de datos
            return Optional.of(almacenRepository.save(almacenDB));
        }
        return productOptional;
    }

    @Transactional
    @Override
    public Optional<Almacen> eliminar(Long id) {
        Optional<Almacen> aOptional = almacenRepository.findById(id);
        aOptional.ifPresent(alDB -> {
            almacenRepository.delete(alDB);
        });
        return aOptional;
    }

@Transactional
public List<String> traspasarMedicamentos(List<TraspasoRequest> traspasos) {
    List<String> mensajes = new ArrayList<>();

    if (traspasos.isEmpty()) {
        mensajes.add("No se recibieron datos de traspaso.");
        return mensajes;
    }

    for (TraspasoRequest traspaso : traspasos) {
        Long almacenOrigenId = traspaso.getAlmacenOrigenId();
        Long almacenDestinoId = traspaso.getAlmacenDestinoId();
        Long medicamentoId = traspaso.getMedicamentoId();
        Integer cantidadSolicitada = traspaso.getCantidad();

        if (almacenOrigenId.equals(almacenDestinoId)) {
            mensajes.add("Los IDs del almacén origen y destino no pueden ser iguales.");
            continue;
        }

        try {
            Almacen almacenOrigen = almacenRepository.findById(almacenOrigenId)
                    .orElseThrow(() -> new RuntimeException("Almacén origen no encontrado."));
            Almacen almacenDestino = almacenRepository.findById(almacenDestinoId)
                    .orElseThrow(() -> new RuntimeException("Almacén destino no encontrado."));

            // Buscar stock en origen
            DetalleAlmacen detalleOrigen = detalleAlmacenRepository
                    .findById(new DetalleAlmacenId(almacenOrigenId, medicamentoId))
                    .orElseThrow(() -> new RuntimeException("El material con ID " + medicamentoId + " no existe en el almacén origen."));

            int cantidadDisponible = detalleOrigen.getStock();
            int cantidadATraspasar = Math.min(cantidadSolicitada, cantidadDisponible);

            if (cantidadATraspasar <= 0) {
                throw new RuntimeException("No hay stock disponible del material con ID " + medicamentoId + " en el almacén origen.");
            }

            // Buscar o crear en destino
            DetalleAlmacen detalleDestino = detalleAlmacenRepository
                    .findById(new DetalleAlmacenId(almacenDestinoId, medicamentoId))
                    .orElse(null);

            if (detalleDestino == null) {
                detalleDestino = new DetalleAlmacen();
                detalleDestino.setId(new DetalleAlmacenId(almacenDestinoId, medicamentoId));
                detalleDestino.setStock(0);
                detalleDestino.setAlmacen(almacenDestino);
                detalleDestino.setMedicamento(detalleOrigen.getMedicamento());
                detalleDestino.setEstado("DISPONIBLE");
            }

            // Actualizar stock
            detalleOrigen.setStock(cantidadDisponible - cantidadATraspasar);
            detalleDestino.setStock(detalleDestino.getStock() + cantidadATraspasar);

            // Actualizar estados
            if (detalleOrigen.getStock() == 0) {
                detalleOrigen.setEstado("NO DISPONIBLE");
            }
            if (detalleDestino.getStock() > 0) {
                detalleDestino.setEstado("DISPONIBLE");
            }

            // Guardar cambios
            detalleAlmacenRepository.save(detalleOrigen);
            detalleAlmacenRepository.save(detalleDestino);

            // Guardar historial
            HistorialAjuste historial = new HistorialAjuste();
            historial.setAlmacenOrigenId(almacenOrigenId);
            historial.setAlmacenDestinoId(almacenDestinoId);
            historial.setMedicamentoId(medicamentoId);
            historial.setCantidad(cantidadATraspasar);
            historial.setFecha(LocalDateTime.now());

            historialAjusteRepository.save(historial);

            Medicamento medicamento = detalleOrigen.getMedicamento();
            mensajes.add("Se traspasaron " + cantidadATraspasar + " unidades del material " +
                    medicamento.getNombre() + " (ID: " + medicamentoId + ") " +
                    "desde el almacén " + almacenOrigen.getNombre() +
                    " hacia el almacén " + almacenDestino.getNombre() + ".");

        } catch (RuntimeException e) {
            mensajes.add("Error con el traspaso del material ID " + medicamentoId + ": " + e.getMessage());
        }
    }

    return mensajes;
}

@Transactional
public List<String> traspasarMedicamentos1(List<TraspasoRequest> traspasos) {
    List<String> mensajes = new ArrayList<>();

    // Buscar los dos almacenes por ID
    Almacen almacen1 = almacenRepository.findById(1L)
            .orElseThrow(() -> new RuntimeException("Almacén 1 no encontrado."));
    Almacen almacen2 = almacenRepository.findById(2L)
            .orElseThrow(() -> new RuntimeException("Almacén 2 no encontrado."));

    for (TraspasoRequest traspaso : traspasos) {
        Long medicamentoId = traspaso.getMedicamentoId();
        Integer cantidadSolicitada = traspaso.getCantidad();

        try {
            // Buscar el medicamento en el almacén 2
            DetalleAlmacen detalleAlmacen2 = detalleAlmacenRepository
                    .findById(new DetalleAlmacenId(2L, medicamentoId))
                    .orElseThrow(() -> new RuntimeException(
                            "El medicamento con ID " + medicamentoId + " no existe en el almacén 2."));

            // Determinar la cantidad a traspasar (lo que haya disponible si es menor a la solicitada)
            int cantidadDisponible = detalleAlmacen2.getStock();
            int cantidadATraspasar = Math.min(cantidadSolicitada, cantidadDisponible);

            if (cantidadATraspasar <= 0) {
                throw new RuntimeException("No hay stock disponible del medicamento con ID " + medicamentoId
                        + " en el almacén 2.");
            }

            // Buscar el medicamento en el almacén 1
            DetalleAlmacen detalleAlmacen1 = detalleAlmacenRepository
                    .findById(new DetalleAlmacenId(1L, medicamentoId))
                    .orElse(null);

            // Si el medicamento no existe en el almacén 1, crearlo
            if (detalleAlmacen1 == null) {
                detalleAlmacen1 = new DetalleAlmacen();
                detalleAlmacen1.setId(new DetalleAlmacenId(1L, medicamentoId));
                detalleAlmacen1.setStock(0); // Inicialmente sin stock
                detalleAlmacen1.setAlmacen(almacen1);
                detalleAlmacen1.setMedicamento(detalleAlmacen2.getMedicamento());
                detalleAlmacen1.setEstado("DISPONIBLE");
            }

            // Actualizar el stock en ambos almacenes
            detalleAlmacen2.setStock(cantidadDisponible - cantidadATraspasar);
            detalleAlmacen1.setStock(detalleAlmacen1.getStock() + cantidadATraspasar);

            // Si el stock del almacén 2 llega a 0, cambiar su estado a "NO DISPONIBLE"
            if (detalleAlmacen2.getStock() == 0) {
                detalleAlmacen2.setEstado("NO DISPONIBLE");
            }

            // Si hay stock disponible en el almacén 1, asegurarse de que su estado sea "DISPONIBLE"
            if (detalleAlmacen1.getStock() > 0) {
                detalleAlmacen1.setEstado("DISPONIBLE");
            }

            // Guardar los cambios
            detalleAlmacenRepository.save(detalleAlmacen2);
            detalleAlmacenRepository.save(detalleAlmacen1);

            // Guardar registro en historial de ajustes (sin motivo)
            HistorialAjuste historial = new HistorialAjuste();
            historial.setAlmacenOrigenId(2L);
            historial.setAlmacenDestinoId(1L);
            historial.setMedicamentoId(medicamentoId);
            historial.setCantidad(cantidadATraspasar);
            historial.setFecha(LocalDateTime.now());

            historialAjusteRepository.save(historial);

            // Agregar mensaje de éxito
            Medicamento medicamento = detalleAlmacen2.getMedicamento();
            mensajes.add("Se traspasaron " + cantidadATraspasar + " unidades del material "
                    + medicamento.getNombre() + " (ID: " + medicamentoId + ") .");
        } catch (RuntimeException e) {
            // Capturar errores específicos de cada traspaso y agregar al mensaje
            mensajes.add("Error con el material ID " + medicamentoId + ": " + e.getMessage());
        }
    }

    return mensajes;
}

    public boolean existeElAlmacen(String almacenNombre) {
        return almacenRepository.existsByNombre(almacenNombre);
    }

    @Transactional(readOnly = true)
    public AlmacenMedicamentosDTO listarMedicamentosPorAlmacen(Long almacenId) {
        // Buscar el almacén por su ID
        Almacen almacen = almacenRepository.findById(almacenId)
                .orElseThrow(() -> new RuntimeException("Almacén no encontrado con ID: " + almacenId));

        // Convertir los DetalleAlmacen a MedicamentoDTO
        List<AlmacenMedicamentosDTO.MedicamentoDTO> medicamentosDTO = new ArrayList<>();
        for (DetalleAlmacen detalle : almacen.getDetalleAlmacenes()) {
            Medicamento medicamento = detalle.getMedicamento();
            medicamentosDTO.add(new AlmacenMedicamentosDTO.MedicamentoDTO(medicamento.getNombre(), detalle.getStock()));
        }

        // Retornar el DTO con el nombre del almacén y la lista de medicamentos con su
        // stock
        return new AlmacenMedicamentosDTO(almacen.getNombre(), medicamentosDTO);
    }




    @Transactional
public List<String> ajustarMedicamentosAlmacen1a23(List<TraspasoRequest> traspasos) {
    List<String> mensajes = new ArrayList<>();

    // Buscar los dos almacenes por ID
    Almacen almacen1 = almacenRepository.findById(1L)
            .orElseThrow(() -> new RuntimeException("Almacén 1 no encontrado."));
    Almacen almacen2 = almacenRepository.findById(2L)
            .orElseThrow(() -> new RuntimeException("Almacén 2 no encontrado."));

    for (TraspasoRequest traspaso : traspasos) {
        Long medicamentoId = traspaso.getMedicamentoId();
        Integer cantidadSolicitada = traspaso.getCantidad();

        try {
            // Buscar el medicamento en el almacén 1
            DetalleAlmacen detalleAlmacen1 = detalleAlmacenRepository
                    .findById(new DetalleAlmacenId(1L, medicamentoId))
                    .orElseThrow(() -> new RuntimeException(
                            "El material con ID " + medicamentoId + " no existe en el almacén 1."));

            // Determinar la cantidad a traspasar
            int cantidadDisponible = detalleAlmacen1.getStock();
            int cantidadATraspasar = Math.min(cantidadSolicitada, cantidadDisponible);

            if (cantidadATraspasar <= 0) {
                throw new RuntimeException("No hay stock disponible del material con ID " + medicamentoId
                        + " en el almacén 1.");
            }

            // Buscar o crear el medicamento en el almacén 2
            DetalleAlmacen detalleAlmacen2 = detalleAlmacenRepository
                    .findById(new DetalleAlmacenId(2L, medicamentoId))
                    .orElse(null);

            if (detalleAlmacen2 == null) {
                detalleAlmacen2 = new DetalleAlmacen();
                detalleAlmacen2.setId(new DetalleAlmacenId(2L, medicamentoId));
                detalleAlmacen2.setStock(0);
                detalleAlmacen2.setAlmacen(almacen2);
                detalleAlmacen2.setMedicamento(detalleAlmacen1.getMedicamento());
                detalleAlmacen2.setEstado("DISPONIBLE");
            }

            // Actualizar el stock en ambos almacenes
            detalleAlmacen1.setStock(cantidadDisponible - cantidadATraspasar);
            detalleAlmacen2.setStock(detalleAlmacen2.getStock() + cantidadATraspasar);

            // Cambiar estado si es necesario
            if (detalleAlmacen1.getStock() == 0) {
                detalleAlmacen1.setEstado("NO DISPONIBLE");
            }
            if (detalleAlmacen2.getStock() > 0) {
                detalleAlmacen2.setEstado("DISPONIBLE");
            }

            // Guardar cambios
            detalleAlmacenRepository.save(detalleAlmacen1);
            detalleAlmacenRepository.save(detalleAlmacen2);

            Medicamento medicamento = detalleAlmacen1.getMedicamento();
            mensajes.add("Se ajustaron " + cantidadATraspasar + " unidades del material "
                    + medicamento.getNombre() + " (ID: " + medicamentoId + ") .");


                    

        } catch (RuntimeException e) {
            mensajes.add("Error con el material ID " + medicamentoId + ": " + e.getMessage());
        }
    }

    return mensajes;
}


@Transactional
public List<String> ajustarMedicamentosAlmacen1a2(List<TraspasoRequest> traspasos) {
    List<String> mensajes = new ArrayList<>();

    Almacen almacen1 = almacenRepository.findById(1L)
            .orElseThrow(() -> new RuntimeException("Almacén 1 no encontrado."));
    Almacen almacen2 = almacenRepository.findById(2L)
            .orElseThrow(() -> new RuntimeException("Almacén 2 no encontrado."));

    for (TraspasoRequest traspaso : traspasos) {
        Long medicamentoId = traspaso.getMedicamentoId();
        Integer cantidadSolicitada = traspaso.getCantidad();

        try {
            DetalleAlmacen detalleAlmacen1 = detalleAlmacenRepository
                    .findById(new DetalleAlmacenId(1L, medicamentoId))
                    .orElseThrow(() -> new RuntimeException(
                            "El material con ID " + medicamentoId + " no existe en el almacén 1."));

            int cantidadDisponible = detalleAlmacen1.getStock();
            int cantidadATraspasar = Math.min(cantidadSolicitada, cantidadDisponible);

            if (cantidadATraspasar <= 0) {
                throw new RuntimeException("No hay stock disponible del material con ID " + medicamentoId
                        + " en el almacén 1.");
            }

            DetalleAlmacen detalleAlmacen2 = detalleAlmacenRepository
                    .findById(new DetalleAlmacenId(2L, medicamentoId))
                    .orElse(null);

            if (detalleAlmacen2 == null) {
                detalleAlmacen2 = new DetalleAlmacen();
                detalleAlmacen2.setId(new DetalleAlmacenId(2L, medicamentoId));
                detalleAlmacen2.setStock(0);
                detalleAlmacen2.setAlmacen(almacen2);
                detalleAlmacen2.setMedicamento(detalleAlmacen1.getMedicamento());
                detalleAlmacen2.setEstado("DISPONIBLE");
            }

            detalleAlmacen1.setStock(cantidadDisponible - cantidadATraspasar);
            detalleAlmacen2.setStock(detalleAlmacen2.getStock() + cantidadATraspasar);

            if (detalleAlmacen1.getStock() == 0) {
                detalleAlmacen1.setEstado("NO DISPONIBLE");
            }
            if (detalleAlmacen2.getStock() > 0) {
                detalleAlmacen2.setEstado("DISPONIBLE");
            }

            detalleAlmacenRepository.save(detalleAlmacen1);
            detalleAlmacenRepository.save(detalleAlmacen2);

            Medicamento medicamento = detalleAlmacen1.getMedicamento();
            mensajes.add("Se ajustaron " + cantidadATraspasar + " unidades del material "
                    + medicamento.getNombre() + " (ID: " + medicamentoId + ").");

            // ✅ REGISTRAR EN HISTORIAL
            HistorialAjuste historial = new HistorialAjuste();
            historial.setAlmacenOrigenId(1L); // Ajusta según tu lógica
historial.setAlmacenDestinoId(2L);
            historial.setMedicamentoId(medicamentoId);
            historial.setCantidad(cantidadATraspasar);
            historial.setMotivo(traspaso.getMotivo() != null ? traspaso.getMotivo() : "Sin motivo especificado");
            historial.setFecha(LocalDateTime.now());
           // historial.setTipo ("AJUSTE");

            historialAjusteRepository.save(historial);  // ✅ nombre correcto (todo en minúscula)HistorialAjusteRepository.save(historial);
            


        } catch (RuntimeException e) {
            mensajes.add("Error con el material ID " + medicamentoId + ": " + e.getMessage());
        }
    }

    return mensajes;
}

@Transactional
public List<String> ajustarStockPorMotivo(Long almacenId, List<TraspasoRequest> ajustes) {
    List<String> mensajes = new ArrayList<>();

    Almacen almacen = almacenRepository.findById(almacenId)
            .orElseThrow(() -> new RuntimeException("Almacén no encontrado."));

    for (TraspasoRequest ajuste : ajustes) {
        Long medicamentoId = ajuste.getMedicamentoId();
        int cantidad = ajuste.getCantidad();
        String motivo = ajuste.getMotivo() != null ? ajuste.getMotivo() : "Sin motivo especificado";
        String estadoAjuste = ajuste.getEstado() != null ? ajuste.getEstado().toUpperCase() : "EGRESO";

        try {
            DetalleAlmacen detalle = detalleAlmacenRepository
                    .findById(new DetalleAlmacenId(almacenId, medicamentoId))
                    .orElseThrow(() -> new RuntimeException("El material no existe en el almacén."));

            int stockActual = detalle.getStock();

            if (estadoAjuste.equals("EGRESO")) {
                if (cantidad > stockActual) {
                    throw new RuntimeException("Stock insuficiente para ajustar " + cantidad + " unidades.");
                }
                detalle.setStock(stockActual - cantidad);
            } else if (estadoAjuste.equals("INGRESO")) {
                detalle.setStock(stockActual + cantidad);
            } else {
                throw new RuntimeException("Estado de ajuste no válido: debe ser 'INGRESO' o 'EGRESO'.");
            }

            // Actualizar estado del medicamento en almacén
            if (detalle.getStock() == 0) {
                detalle.setEstado("NO DISPONIBLE");
            } else {
                detalle.setEstado("DISPONIBLE");
            }

            detalleAlmacenRepository.save(detalle);

            // Registrar el historial del ajuste
            HistorialAjuste historial = new HistorialAjuste();
            historial.setAlmacenOrigenId(estadoAjuste.equals("EGRESO") ? almacenId : null);
            historial.setAlmacenDestinoId(estadoAjuste.equals("INGRESO") ? almacenId : null);
            historial.setMedicamentoId(medicamentoId);
            historial.setCantidad(cantidad);
            historial.setMotivo(motivo);
            historial.setEstado(estadoAjuste); // nuevo campo
            historial.setFecha(LocalDateTime.now());
            historialAjusteRepository.save(historial);

            String mensaje = String.format("%s de %d unidades del material ID %d. Motivo: %s",
                    estadoAjuste.equals("INGRESO") ? "Ingreso" : "Egreso",
                    cantidad,
                    medicamentoId,
                    motivo);
            mensajes.add(mensaje);

        } catch (RuntimeException e) {
            mensajes.add("Error con el material ID " + medicamentoId + ": " + e.getMessage());
        }
    }

    return mensajes;
}

}
