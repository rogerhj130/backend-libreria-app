package com.springboot.backend.andres.usersapp.usersbackend.entities;

import java.util.HashSet; // Usaremos Set
import java.util.List;
import java.util.Set;    // Usaremos Set

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// import com.fasterxml.jackson.annotation.JsonProperty; // No es necesaria aquí si no ocultas nada

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column; // Para la anotación @Column
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="roles") // El nombre de la tabla 'roles' está bien
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Aseguramos que el nombre del rol sea único y no nulo.
    // Los nombres de roles suelen ser identificadores clave, como "ROLE_ADMIN", "ROLE_VENTAS".
    @Column(unique = true, nullable = false)
    private String name;

    // La relación con RolePermiso.
    // Cambiamos List a Set para evitar duplicados y mejorar el rendimiento.
    // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Generalmente, querrás ver los permisos de un rol. Solo si es estrictamente necesario ocultarlos del JSON, la usarías.
    @JsonIgnoreProperties({"role", "handler", "hibernateLazyInitializer"}) // Asegúrate de ignorar 'role' de RolePermiso.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "role")
    private Set<RolePermiso> rolePermissions; // Cambiado a Set

    public Role() {
        this.rolePermissions = new HashSet<>(); // Inicializa con HashSet
    }

    public Role(String name) {
        this(); // Llama al constructor por defecto para inicializar rolePermissions
        this.name = name;
    }

    // --- Getters y Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<RolePermiso> getRolePermissions() { // Getter y Setter para Set
        return rolePermissions;
    }

    public void setRolePermissions(Set<RolePermiso> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    // Opcional: métodos de ayuda para añadir/eliminar permisos de roles
    public void addRolePermission(RolePermiso rolePermission) {
        this.rolePermissions.add(rolePermission);
        rolePermission.setRole(this); // Asegura la bidireccionalidad si RolePermiso necesita conocer el Role
    }

    public void removeRolePermission(RolePermiso rolePermission) {
        this.rolePermissions.remove(rolePermission);
        rolePermission.setRole(null); // Desvincula
    }
}