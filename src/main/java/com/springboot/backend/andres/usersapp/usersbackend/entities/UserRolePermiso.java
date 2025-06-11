package com.springboot.backend.andres.usersapp.usersbackend.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "users_roles_permiso", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "role_permission_id"})
})
public class UserRolePermiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_permission_id", nullable = false)
    private RolePermiso rolePermission;

    public UserRolePermiso() {
    }

    public UserRolePermiso(User user, RolePermiso rolePermission) {
        this.user = user;
        this.rolePermission = rolePermission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RolePermiso getRolePermission() {
        return rolePermission;
    }

    public void setRolePermission(RolePermiso rolePermission) {
        this.rolePermission = rolePermission;
    }
}
