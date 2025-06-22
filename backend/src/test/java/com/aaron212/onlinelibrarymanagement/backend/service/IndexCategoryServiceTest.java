package com.aaron212.onlinelibrarymanagement.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.aaron212.onlinelibrarymanagement.backend.model.IndexCategory;
import com.aaron212.onlinelibrarymanagement.backend.repository.IndexCategoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IndexCategoryServiceTest {

    @Mock
    private IndexCategoryRepository indexCategoryRepository;

    @InjectMocks
    private IndexCategoryService indexCategoryService;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(indexCategoryRepository);
    }

    @Test
    void testAddCategoryWithHierarchy_Simple() {
        // Arrange
        String categoryCode = "X1234";

        // Mock that no categories exist initially
        when(indexCategoryRepository.findByIndexCode(any())).thenReturn(Optional.empty());

        // Mock save operations to return the entity with an ID
        when(indexCategoryRepository.save(any(IndexCategory.class))).thenAnswer(invocation -> {
            IndexCategory category = invocation.getArgument(0);
            // Simulate setting an ID as if saved to database
            if (category.getId() == null) {
                IndexCategory savedCategory = new IndexCategory();
                savedCategory.setId(1L);
                savedCategory.setIndexCode(category.getIndexCode());
                savedCategory.setName(category.getName());
                savedCategory.setParent(category.getParent());
                return savedCategory;
            }
            return category;
        });

        // Act
        IndexCategory result = indexCategoryService.addCategoryWithHierarchy(categoryCode);

        // Assert
        assertNotNull(result);
        assertEquals(categoryCode, result.getIndexCode());

        // Verify that the hierarchy was created: root, X, X1, X12, X123, X1234
        verify(indexCategoryRepository, times(6)).save(any(IndexCategory.class));
    }

    @Test
    void testAddCategoryWithHierarchy_WithDecimal() {
        // Arrange
        String categoryCode = "X1234.567";

        // Mock that no categories exist initially
        when(indexCategoryRepository.findByIndexCode(any())).thenReturn(Optional.empty());

        // Mock save operations
        when(indexCategoryRepository.save(any(IndexCategory.class))).thenAnswer(invocation -> {
            IndexCategory category = invocation.getArgument(0);
            IndexCategory savedCategory = new IndexCategory();
            savedCategory.setId(1L);
            savedCategory.setIndexCode(category.getIndexCode());
            savedCategory.setName(category.getName());
            savedCategory.setParent(category.getParent());
            return savedCategory;
        });

        // Act
        IndexCategory result = indexCategoryService.addCategoryWithHierarchy(categoryCode);

        // Assert
        assertNotNull(result);
        assertEquals(categoryCode, result.getIndexCode());

        // Verify that the hierarchy was created: root, X, X1, X12, X123, X1234, X1234.567
        verify(indexCategoryRepository, times(7)).save(any(IndexCategory.class));
    }

    @Test
    void testAddCategoryWithHierarchy_ExistingCategory() {
        // Arrange
        String categoryCode = "X1234";
        IndexCategory existingCategory = new IndexCategory();
        existingCategory.setId(1L);
        existingCategory.setIndexCode(categoryCode);
        existingCategory.setName("Existing Category");

        when(indexCategoryRepository.findByIndexCode(eq(categoryCode))).thenReturn(Optional.of(existingCategory));

        // Mock that parent categories don't exist
        when(indexCategoryRepository.findByIndexCode(eq("root"))).thenReturn(Optional.empty());
        when(indexCategoryRepository.findByIndexCode(eq("X"))).thenReturn(Optional.empty());
        when(indexCategoryRepository.findByIndexCode(eq("X1"))).thenReturn(Optional.empty());
        when(indexCategoryRepository.findByIndexCode(eq("X12"))).thenReturn(Optional.empty());
        when(indexCategoryRepository.findByIndexCode(eq("X123"))).thenReturn(Optional.empty());

        when(indexCategoryRepository.save(any(IndexCategory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        IndexCategory result = indexCategoryService.addCategoryWithHierarchy(categoryCode);

        // Assert
        assertNotNull(result);
        assertEquals(categoryCode, result.getIndexCode());
        assertEquals(existingCategory, result);

        // Verify that parent categories were created (5) plus the existing category parent update (1) = 6 saves
        verify(indexCategoryRepository, times(6)).save(any(IndexCategory.class));
    }

    @Test
    void testAddCategoryWithHierarchy_InvalidFormat() {
        // Arrange
        String invalidCategoryCode = "InvalidFormat";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> indexCategoryService.addCategoryWithHierarchy(invalidCategoryCode));

        assertTrue(exception.getMessage().contains("Invalid category format"));
    }

    @Test
    void testAddCategoryWithHierarchy_NullInput() {
        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> indexCategoryService.addCategoryWithHierarchy(null));

        assertTrue(exception.getMessage().contains("Index code cannot be null or empty"));
    }

    @Test
    void testAddCategoryWithHierarchy_EmptyInput() {
        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> indexCategoryService.addCategoryWithHierarchy(""));

        assertTrue(exception.getMessage().contains("Index code cannot be null or empty"));
    }
}
