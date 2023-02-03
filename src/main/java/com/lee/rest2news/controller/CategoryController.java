package com.lee.rest2news.controller;

import com.lee.rest2news.payload.CategoryDto;
import com.lee.rest2news.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }

    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable("id") Long categoryId) {
        return categoryService.getCategory(categoryId);
    }

    @GetMapping
    public List<CategoryDto> getCategories() {
        return categoryService.getAllCategories();
    }

    @PutMapping("{id}")
    public CategoryDto updateCategory(@RequestBody CategoryDto categoryDto,
                                      @PathVariable("id") Long categoryId) {
        return categoryService.updateCategory(categoryDto, categoryId);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
    }

}
