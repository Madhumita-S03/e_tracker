package com.family.expensetracker.controller;

import com.family.expensetracker.dto.CategoryRequest;
import com.family.expensetracker.dto.CategoryResponse;
import com.family.expensetracker.entity.Category;
import com.family.expensetracker.exception.BadRequestException;
import com.family.expensetracker.exception.ResourceNotFoundException;
import com.family.expensetracker.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Manage expense categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final com.family.expensetracker.repository.ExpenseRepository expenseRepository;

    @GetMapping
    @Operation(summary = "List all categories")
    public ResponseEntity<List<CategoryResponse>> getAll() {
        List<CategoryResponse> categories = categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by id")
    public ResponseEntity<CategoryResponse> getById(@PathVariable("id") Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return ResponseEntity.ok(toResponse(category));
    }

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("A category with this name already exists");
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .colorCode(request.getColorCode())
                .icon(request.getIcon())
                .defaultCategory(false)
                .build();
        Category saved = categoryRepository.save(category);
        return ResponseEntity.status(201).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing category")
    public ResponseEntity<CategoryResponse> update(@PathVariable("id") Long id, @Valid @RequestBody CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setColorCode(request.getColorCode());
        category.setIcon(request.getIcon());

        Category updated = categoryRepository.save(category);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        if (category.isDefaultCategory()) {
            throw new BadRequestException("Default categories cannot be deleted");
        }
        if (expenseRepository.existsByCategoryId(id)) {
            throw new BadRequestException("This category is used by existing expenses and cannot be deleted");
        }
        categoryRepository.delete(category);
        return ResponseEntity.noContent().build();
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .colorCode(category.getColorCode())
                .icon(category.getIcon())
                .defaultCategory(category.isDefaultCategory())
                .build();
    }
}
