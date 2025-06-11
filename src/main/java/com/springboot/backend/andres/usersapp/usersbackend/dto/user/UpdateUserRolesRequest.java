package com.springboot.backend.andres.usersapp.usersbackend.dto.user;

import java.util.List;

public class UpdateUserRolesRequest {
    private Long userId;
    private List<Long> roleIds; // <-- CAMBIADO DE rolePermissionIds A roleIds

    public UpdateUserRolesRequest() {
    }

    public UpdateUserRolesRequest(Long userId, List<Long> roleIds) {
        this.userId = userId;
        this.roleIds = roleIds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // <-- CAMBIADO DE getRolePermissionIds() A getRoleIds()
    public List<Long> getRoleIds() {
        return roleIds;
    }

    // <-- CAMBIADO DE setRolePermissionIds() A setRoleIds()
    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}

