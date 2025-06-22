package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.model.IndexCategory;
import com.aaron212.onlinelibrarymanagement.backend.service.IndexCategoryService;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class IndexCategoryController {

    private final IndexCategoryService indexCategoryService;

    public IndexCategoryController(IndexCategoryService indexCategoryService) {
        this.indexCategoryService = indexCategoryService;
    }

    /**
     * Add a category with its full hierarchy
     */
    @PostMapping
    public ResponseEntity<IndexCategory> addCategory(@RequestParam String indexCode) {
        try {
            IndexCategory category = indexCategoryService.addCategoryWithHierarchy(indexCode);
            return ResponseEntity.status(HttpStatus.CREATED).body(category);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all categories
     */
    @GetMapping
    public ResponseEntity<List<IndexCategory>> getAllCategories() {
        List<IndexCategory> categories = indexCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Get a specific category by index code
     */
    @GetMapping("/{indexCode}")
    public ResponseEntity<IndexCategory> getCategoryByIndexCode(@PathVariable String indexCode) {
        Optional<IndexCategory> category = indexCategoryService.findByIndexCode(indexCode);
        return category.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Check if a category exists
     */
    @GetMapping("/{indexCode}/exists")
    public ResponseEntity<Boolean> categoryExists(@PathVariable String indexCode) {
        boolean exists = indexCategoryService.existsByIndexCode(indexCode);
        return ResponseEntity.ok(exists);
    }
}
