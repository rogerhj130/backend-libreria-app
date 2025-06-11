package com.springboot.backend.andres.usersapp.usersbackend.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.springboot.backend.andres.usersapp.usersbackend.entities.UserRolePermiso;


public interface UserRolePermissionRepository extends CrudRepository<UserRolePermiso, Long> {
    List<UserRolePermiso> findByUserId(Long userId);
}
