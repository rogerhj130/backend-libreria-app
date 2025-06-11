package com.springboot.backend.andres.usersapp.usersbackend.repositories;

import org.springframework.data.repository.CrudRepository;

import com.springboot.backend.andres.usersapp.usersbackend.entities.Permiso;
import com.springboot.backend.andres.usersapp.usersbackend.entities.RolePermiso;


public interface RolePermissionRepository extends CrudRepository<RolePermiso, Long>{
    boolean existsByRoleAndPermission(com.springboot.backend.andres.usersapp.usersbackend.entities.Role role, Permiso permiso);
}
