package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.model.IndexCategory;
import com.aaron212.onlinelibrarymanagement.backend.repository.IndexCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional
public class IndexCategoryService {

    private static final String ROOT_CATEGORY = "root";
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("\\w\\d+(\\.\\d+)?");
    
    private final IndexCategoryRepository indexCategoryRepository;

    public IndexCategoryService(IndexCategoryRepository indexCategoryRepository) {
        this.indexCategoryRepository = indexCategoryRepository;
    }

    /**
     * Adds a category and all its parent categories to the database if they don't exist.
     * For example, adding "X1234.567" will create:
     * X1234.567 -> X1234 -> X123 -> X12 -> X1 -> X -> root
     * 
     * @param indexCode the category code to add (e.g., "X1234.567")
     * @return the IndexCategory entity for the specified indexCode
     */
    public IndexCategory addCategoryWithHierarchy(String indexCode) {
        if (indexCode == null || indexCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Index code cannot be null or empty");
        }

        // Validate the category format
        if (!CATEGORY_PATTERN.matcher(indexCode).matches()) {
            throw new IllegalArgumentException("Invalid category format: " + indexCode + 
                ". Expected format: \\w\\d+(\\.\\d+)? (e.g., X1234 or X1234.567)");
        }

        // Generate the hierarchy from most specific to root
        List<String> hierarchy = generateCategoryHierarchy(indexCode);
        
        // Ensure root category exists
        IndexCategory rootCategory = ensureRootExists();
        
        // Create categories from root to most specific, ensuring parent relationships
        IndexCategory currentParent = rootCategory;
        
        for (int i = hierarchy.size() - 2; i >= 0; i--) { // Skip root (last element)
            String categoryCode = hierarchy.get(i);
            IndexCategory category = findOrCreateCategory(categoryCode, currentParent);
            currentParent = category;
        }
        
        return currentParent; // This will be the most specific category
    }

    /**
     * Generates the hierarchy for a given category code.
     * For "X1234.567", returns ["X1234.567", "X1234", "X123", "X12", "X1", "X", "root"]
     */
    private List<String> generateCategoryHierarchy(String indexCode) {
        List<String> hierarchy = new ArrayList<>();
        hierarchy.add(indexCode);
        
        String current = indexCode;
        
        // Remove decimal part if present
        if (current.contains(".")) {
            current = current.substring(0, current.indexOf('.'));
            hierarchy.add(current);
        }
        
        // Generate parent categories by removing digits from the end
        while (current.length() > 1) {
            // Remove the last character if it's a digit
            if (Character.isDigit(current.charAt(current.length() - 1))) {
                current = current.substring(0, current.length() - 1);
                hierarchy.add(current);
            } else {
                break;
            }
        }
        
        // Add root
        hierarchy.add(ROOT_CATEGORY);
        
        return hierarchy;
    }

    /**
     * Ensures the root category exists in the database
     */
    private IndexCategory ensureRootExists() {
        return findOrCreateCategory(ROOT_CATEGORY, null);
    }

    /**
     * Finds an existing category or creates a new one with the specified parent
     */
    private IndexCategory findOrCreateCategory(String indexCode, IndexCategory parent) {
        Optional<IndexCategory> existing = indexCategoryRepository.findByIndexCode(indexCode);
        
        if (existing.isPresent()) {
            IndexCategory category = existing.get();
            // Update parent if it's not set and we have a parent
            if (category.getParent() == null && parent != null) {
                category.setParent(parent);
                category = indexCategoryRepository.save(category);
            }
            return category;
        }
        
        // Create new category
        IndexCategory newCategory = new IndexCategory();
        newCategory.setIndexCode(indexCode);
        newCategory.setParent(parent);
        
        // Set a default name based on the index code
        if (ROOT_CATEGORY.equals(indexCode)) {
            newCategory.setName("Root Category");
        } else {
            newCategory.setName("Category " + indexCode);
        }
        
        return indexCategoryRepository.save(newCategory);
    }

    /**
     * Finds a category by index code
     */
    @Transactional(readOnly = true)
    public Optional<IndexCategory> findByIndexCode(String indexCode) {
        return indexCategoryRepository.findByIndexCode(indexCode);
    }

    /**
     * Checks if a category exists
     */
    @Transactional(readOnly = true)
    public boolean existsByIndexCode(String indexCode) {
        return indexCategoryRepository.existsByIndexCode(indexCode);
    }

    /**
     * Gets all categories
     */
    @Transactional(readOnly = true)
    public List<IndexCategory> getAllCategories() {
        return indexCategoryRepository.findAll();
    }
}