package com.springboot.backend.andres.usersapp.usersbackend.controllers;

import java.util.List;
import java.util.stream.Collectors;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

//import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
//import org.springframework.data.domain.PageRequest;
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

import com.springboot.backend.andres.usersapp.usersbackend.dto.role.AssignRoleRequest;
import com.springboot.backend.andres.usersapp.usersbackend.dto.role.RoleWithPermissionsDto;
import com.springboot.backend.andres.usersapp.usersbackend.dto.user.AssignRolePermissionsToUserRequest;
import com.springboot.backend.andres.usersapp.usersbackend.dto.user.UpdateUserRolesRequest;
import com.springboot.backend.andres.usersapp.usersbackend.entities.Role;
import com.springboot.backend.andres.usersapp.usersbackend.services.RoleService;
import com.springboot.backend.andres.usersapp.usersbackend.services.UserService;



@CrossOrigin(origins={"http://localhost:4200"})
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<Role> getRoles() {
        return roleService.findAll();
    }

    @GetMapping("/with-permissions")
    public ResponseEntity<List<RoleWithPermissionsDto>> listRolesWithPermissionsDto() {
        List<RoleWithPermissionsDto> roles = roleService.getRolesWithPermissions();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRol(@PathVariable Long id) {
        return roleService.findById(id)
                .map(role -> ResponseEntity.ok(role))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/page/{page}")
public Page<Role> getRolesPage(@PathVariable int page) {
    Pageable pageable = PageRequest.of(page, 10); // tamaño 10 por página
    return roleService.findAll(pageable); // asegúrate que tu RoleService lo soporte
}

    @PostMapping
    public ResponseEntity<Role> createdRole(@RequestBody Role role) {
        Role createdRole = roleService.save(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    

    // asigna rolePermiso a los users
  //  @PostMapping("/assign-role-permissions")
   // public ResponseEntity<String> assignRolePermissionsToUser(@RequestBody AssignRolePermissionsToUserRequest request) {
     //   userService.assignRolePermissions(request.getUserId(), request.getRolePermissionIds());
       // return ResponseEntity.ok("Permisos asignados correctamente");
   /// }

    
@PostMapping("/assign-role-permissions-to-user1") // Nuevo endpoint para este método
public ResponseEntity<String> assignRolePermissionsToUser(@RequestBody AssignRolePermissionsToUserRequest request) {
    roleService.assignRolePermissionsToUser(request.getUserId(), request.getRolePermissionIds());
    return ResponseEntity.ok("Combinaciones de rol-permiso asignadas al usuario correctamente.");
}

 @PostMapping("/assign-role-permissions-to-user")
    public ResponseEntity<String> assignPermissionsToUser(@RequestBody AssignRolePermissionsToUserRequest request) {
        roleService.assignRolePermissionsToUser(request.getUserId(), request.getRolePermissionIds());
        return ResponseEntity.ok("Permisos asignados correctamente.");
    }

    @PostMapping("/assign-role")
public ResponseEntity<String> assignRoleToUser(@RequestBody AssignRoleRequest request) {
    userService.assignRoleToUser(request.getUserId(), request.getRoleId());
    return ResponseEntity.ok("Rol asignado correctamente al usuario.");
}


  @PutMapping("/update-user-roles")
    // Usa el nuevo DTO aquí
   public ResponseEntity<String> updateUserRoles(@RequestBody UpdateUserRolesRequest request) {
        // Ahora llamas a getRoleIds() que es lo que espera updateUserRoles
        userService.updateUserRoles(request.getUserId(), request.getRoleIds());
        return ResponseEntity.ok("Roles del usuario actualizados correctamente.");
    }









    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        return roleService.update(id, role)
                .map(updateRole -> ResponseEntity.ok(updateRole))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        boolean deleted = roleService.deleteById(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
