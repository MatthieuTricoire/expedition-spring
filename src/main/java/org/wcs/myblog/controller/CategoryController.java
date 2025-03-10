package org.wcs.myblog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wcs.myblog.dto.CategoryDTO;
import org.wcs.myblog.model.Category;
import org.wcs.myblog.service.CategoryService;

@RestController
@RequestMapping("/categories")
public class CategoryController {
  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping
  public ResponseEntity<List<CategoryDTO>> getAllCategories() {
    List<CategoryDTO> categoryDTOs = categoryService.getAllCategories();
    if (categoryDTOs.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(categoryDTOs);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
    CategoryDTO categoryDTO = categoryService.getCategoryById(id);
    if (categoryDTO == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(categoryDTO);
  }

  @PostMapping
  public ResponseEntity<CategoryDTO> createCategory(@RequestBody Category category) {
    CategoryDTO savedCategory = categoryService.createCategory(category);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {

    CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDetails);
    if (updatedCategory == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(updatedCategory);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {

    if (categoryService.deleteCategory(id)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
