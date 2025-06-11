package com.springboot.backend.andres.usersapp.usersbackend.dto.accessory;

public class AccessoryAssignmentCategoryDto {

    private Long id;
    private String name;
    private Double price;
    private String description;
    private String brand;
    private String model;
    private Long categoryId;
    
    public AccessoryAssignmentCategoryDto() {
    }

    public AccessoryAssignmentCategoryDto(Long id, String name, Double price, String description, String brand,
            String model, Long categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.brand = brand;
        this.model = model;
        this.categoryId = categoryId;
    }

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    
}
