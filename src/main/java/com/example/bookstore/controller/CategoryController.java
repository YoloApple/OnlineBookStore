package com.example.bookstore.controller;

import com.example.bookstore.dto.CategoryRequest;
import com.example.bookstore.dto.CategoryResponse;
import com.example.bookstore.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> getAll(){
        return categoryService.getAllCategories();
    }

    @PostMapping
    public ResponseEntity<String> addCategory(@RequestBody CategoryRequest request) {
        categoryService.addCategory(request);
        return ResponseEntity.ok("Thêm danh mục thành công");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id,
                                                 @RequestBody CategoryRequest request) {
        categoryService.updateCategory(id, request);
        return ResponseEntity.ok("Cập nhật danh mục thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Xóa danh mục thành công");
    }
    @PostMapping("/bulk")
    public ResponseEntity<String> addMultipleCategories(@RequestBody List<CategoryRequest> requests) {
        for (CategoryRequest req : requests) {
            categoryService.addCategory(req);
        }
        return ResponseEntity.ok("Thêm nhiều danh mục thành công");
    }

}
