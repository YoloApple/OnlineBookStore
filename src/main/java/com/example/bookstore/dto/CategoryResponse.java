package com.example.bookstore.dto;

import com.example.bookstore.entity.Category;

public class CategoryResponse {
    private Long id;
    private String name;

    public static CategoryResponse fromEntity(Category category){
        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
    //Getter and Setter

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
}
