package com.springboot.backend.andres.usersapp.usersbackend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.backend.andres.usersapp.usersbackend.dto.role.RolePermissionAssignmentDto;
import com.springboot.backend.andres.usersapp.usersbackend.entities.Permiso;
import com.springboot.backend.andres.usersapp.usersbackend.services.PermisoService;
//import com.springboot.backend.andres.usersapp.usersbackend.services.interfaces.PermissionService;


@CrossOrigin(origins={"http://localhost:4200"})
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermisoService permissionService;


    @GetMapping
    public List<Permiso> getPermissions() {
        return permissionService.findAll();
    }
    
    @GetMapping("/page/{page}")
    public ResponseEntity<?> listarPageable(@PathVariable Integer page) {
        Pageable pageable = PageRequest.of(page, 12);
        Page<Permiso> almacen = permissionService.paginarTodo(pageable);
        return ResponseEntity.ok(almacen);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permiso> getPermission(@PathVariable Long id) {
        return permissionService.findById(id)
                .map(permission -> ResponseEntity.ok(permission))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Permiso> createPermission(@RequestBody Permiso permission) {
        Permiso createdPermission = permissionService.save(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPermission);
    }

    // asignamos permisos a los roles
    @PostMapping("/assign-permissions")
    public ResponseEntity<Void> assignPermissionsToRole(
            @RequestBody RolePermissionAssignmentDto dto) {

        permissionService.assignPermissionsToRole(dto.getRoleId(), dto.getPermissionIds());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permiso> updatePermission(@PathVariable Long id, @RequestBody Permiso permission) {
        return permissionService.update(id, permission)
                .map(updatePermission -> ResponseEntity.ok(updatePermission))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        boolean deleted = permissionService.deleteById(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
