package com.springboot.backend.andres.usersapp.usersbackend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.springboot.backend.andres.usersapp.usersbackend.dto.role.RoleWithPermissionsDto;
import com.springboot.backend.andres.usersapp.usersbackend.dto.user.UserWithRolesAndPermissionsDto;
import com.springboot.backend.andres.usersapp.usersbackend.entities.Role;

//import com.universidad.tecno.api_gestion_accesorios.dto.role.RoleWithPermissionsDto;
//import com.universidad.tecno.api_gestion_accesorios.dto.user.UserWithRolesAndPermissionsDto;
//import com.universidad.tecno.api_gestion_accesorios.entities.Role;

public interface RoleService {

    List<Role> findAll();
    Optional<Role> findById(Long id);
    Role save(Role role);
    Optional<Role> update(Long id, Role role);
    boolean deleteById(Long id);

    List<RoleWithPermissionsDto> getRolesWithPermissions();

    List<UserWithRolesAndPermissionsDto> getUsersWithRolesAndPermissions();
     void assignRolePermissionsToUser(Long userId, List<Long> rolePermissionIds);

     public Page<Role> findAll(Pageable pageable);
}
