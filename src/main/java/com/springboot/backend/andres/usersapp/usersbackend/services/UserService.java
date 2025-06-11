package com.springboot.backend.andres.usersapp.usersbackend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import com.springboot.backend.andres.usersapp.usersbackend.dto.role.AssignRoleRequest;
import com.springboot.backend.andres.usersapp.usersbackend.dto.user.UserWithRolesAndPermissionsDto;
import com.springboot.backend.andres.usersapp.usersbackend.entities.User;
import com.springboot.backend.andres.usersapp.usersbackend.models.UserRequest;

public interface UserService {

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    Optional<User> findById(@NonNull Long id);

    User save(User user);

    Optional<User> update(UserRequest user, Long id);

    void deleteById(Long id);
     //asignamos rolesPermisos a los users
    void assignRolePermissions(Long userId, List<Long> rolePermissionIds);

    List<UserWithRolesAndPermissionsDto> getUsersWithRolesAndPermissions();

    //List<AssignRoleRequest>assignRoleToUser();

    void assignRoleToUser(Long userId, Long roleId);

    //void updateRolesForUser(Long userId, List<Long> roleIds);

    void updateUserRoles(Long userId, List<Long> roleIdsToUpdate);
   Optional<User> findByUsername(String username); // ✅ Agrega esta línea
    
}
