package com.TZ.TechZone.services;

import com.TZ.TechZone.dto.CategoryDTO;
import com.TZ.TechZone.entities.Category;
import com.TZ.TechZone.exceptions.ResourceNotFoundException;
import com.TZ.TechZone.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1);
        category.setName("Electronics");
        category.setDescription("Electronic products");

        categoryDTO = new CategoryDTO();
        categoryDTO.setName("Electronics");
        categoryDTO.setDescription("Electronic products");
    }

    @Test
    void createCategory_whenValidData_createsSuccessfully() {
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDTO result = categoryService.createCategory(categoryDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Electronics");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_whenNameExists_throwsIllegalArgumentException() {
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> categoryService.createCategory(categoryDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Une catégorie avec ce nom existe déjà");
    }

    @Test
    void getCategoryById_whenExists_returnsCategory() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        CategoryDTO result = categoryService.getCategoryById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Electronics");
    }

    @Test
    void getCategoryById_whenNotExists_throwsResourceNotFoundException() {
        when(categoryRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Catégorie non trouvée");
    }

    @Test
    void getCategoryByName_whenExists_returnsCategory() {
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(category));

        CategoryDTO result = categoryService.getCategoryByName("Electronics");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Electronics");
    }

    @Test
    void getCategoryByName_whenNotExists_throwsResourceNotFoundException() {
        when(categoryRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryByName("Unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Catégorie non trouvée");
    }

    @Test
    void getAllCategories_returnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        when(categoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(category)));

        Page<CategoryDTO> result = categoryService.getAllCategories(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Electronics");
    }

    @Test
    void getAllCategoriesList_returnsAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryDTO> result = categoryService.getAllCategoriesList();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Electronics");
    }

    @Test
    void updateCategory_withValidData_updatesSuccessfully() {
        CategoryDTO updateDTO = new CategoryDTO();
        updateDTO.setName("Updated Electronics");
        updateDTO.setDescription("Updated description");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName("Updated Electronics")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDTO result = categoryService.updateCategory(1, updateDTO);

        assertThat(result).isNotNull();
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_whenCategoryNotExists_throwsResourceNotFoundException() {
        when(categoryRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory(999, categoryDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Catégorie non trouvée");
    }

    @Test
    void updateCategory_whenNewNameExists_throwsIllegalArgumentException() {
        Category existingCategory = new Category();
        existingCategory.setId(2);
        existingCategory.setName("Gaming");

        CategoryDTO updateDTO = new CategoryDTO();
        updateDTO.setName("Gaming");
        updateDTO.setDescription("New description");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName("Gaming")).thenReturn(Optional.of(existingCategory));

        assertThatThrownBy(() -> categoryService.updateCategory(1, updateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Une catégorie avec ce nom existe déjà");
    }

    @Test
    void updateCategory_withSameName_allowsUpdate() {
        CategoryDTO updateDTO = new CategoryDTO();
        updateDTO.setName("Electronics");
        updateDTO.setDescription("Updated description");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDTO result = categoryService.updateCategory(1, updateDTO);

        assertThat(result).isNotNull();
        verify(categoryRepository).save(any(Category.class));
        verify(categoryRepository, never()).findByName(anyString());
    }

    @Test
    void deleteCategory_whenExists_deletesSuccessfully() {
        when(categoryRepository.existsById(1)).thenReturn(true);

        categoryService.deleteCategory(1);

        verify(categoryRepository).deleteById(1);
    }

    @Test
    void deleteCategory_whenNotExists_throwsResourceNotFoundException() {
        when(categoryRepository.existsById(999)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.deleteCategory(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Catégorie non trouvée");
    }
}
