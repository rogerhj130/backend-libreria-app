package com.springboot.backend.andres.usersapp.usersbackend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.springboot.backend.andres.usersapp.usersbackend.entities.Permiso;
//import com.universidad.tecno.api_gestion_accesorios.entities.Permission;

public interface PermisoService {
    List<Permiso> findAll();
    Page<Permiso> paginarTodo(Pageable pageable);
    Optional<Permiso> findById(Long id);
    Optional<Permiso> update(Long id, Permiso permission);
    Permiso save(Permiso permission);
    boolean deleteById(Long id); 

    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);
}
