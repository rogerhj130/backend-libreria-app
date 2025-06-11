package com.springboot.backend.andres.usersapp.usersbackend.dto.role;

//import java.util.List;

public class AssignRoleRequest {
    private Long userId;
    private Long roleId;
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getRoleId() {
        return roleId;
    }
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    
}
