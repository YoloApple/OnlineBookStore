package com.example.bookstore.service;

import com.example.bookstore.dto.CategoryRequest;
import com.example.bookstore.dto.CategoryResponse;
import com.example.bookstore.entity.Category;
import com.example.bookstore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    public List<CategoryResponse>getAllCategories(){
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void addCategory(CategoryRequest request){
        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Tên danh mục không được để trống");
        }
        if(categoryRepository.existsByName(request.getName())){
            throw new RuntimeException("Tên danh mục đã tồn tại");
        }
        Category category = new Category();
        category.setName(request.getName());
        categoryRepository.save(category);
    }

    public void updateCategory(Long id , CategoryRequest request){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Không tìm thấy danh mục"));
        category.setName(request.getName());
        categoryRepository.save(category);
    }

    public void deleteCategory(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Không tìm thấy danh mục"));
        if(category.getBooks() !=null  && !category.getBooks().isEmpty()){
            throw new RuntimeException("Không thể xóa danh mục vì còn sách liên quan");
        }
        categoryRepository.delete(category);
    }
}
