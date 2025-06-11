package com.springboot.backend.andres.usersapp.usersbackend.dto.purchase;

import java.util.List;

public class CreatePurchaseDto {
    private Long supplierId;
    private Long userId;
    private List<CreatePurchaseDetailDto> purchaseDetails;

    public CreatePurchaseDto() {
    }

    public CreatePurchaseDto(Long supplierId, Long userId, List<CreatePurchaseDetailDto> purchaseDetails) {
        this.supplierId = supplierId;
        this.userId = userId;
        this.purchaseDetails = purchaseDetails;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<CreatePurchaseDetailDto> getPurchaseDetails() {
        return purchaseDetails;
    }

    public void setPurchaseDetails(List<CreatePurchaseDetailDto> purchaseDetails) {
        this.purchaseDetails = purchaseDetails;
    }
    

    
}
