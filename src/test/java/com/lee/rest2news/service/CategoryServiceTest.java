package com.lee.rest2news.service;

import com.lee.rest2news.entity.Category;
import com.lee.rest2news.exception.ResourceNotFoundException;
import com.lee.rest2news.payload.CategoryDto;
import com.lee.rest2news.repository.CategoryRepository;
import com.lee.rest2news.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static com.lee.rest2news.TestConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void addCategory(@Mock CategoryDto inputDto, @Mock CategoryDto outputDto,
                            @Mock Category inputCategory, @Mock Category outputCategory) {
        // given
        when(modelMapper.map(inputDto, Category.class)).thenReturn(inputCategory);
        when(categoryRepository.save(inputCategory)).thenReturn(outputCategory);
        when(modelMapper.map(outputCategory, CategoryDto.class)).thenReturn(outputDto);

        // when
        CategoryDto actualCategoryDto = categoryService.addCategory(inputDto);

        // then
        assertSame(outputDto, actualCategoryDto);
    }

    @Test
    public void getCategoryById_Found_Test(@Mock Category category, @Mock CategoryDto categoryDto) {
        // given
        when(categoryRepository.findById(TEST_ID_1)).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        // when
        CategoryDto actualCategoryDto = categoryService.getCategory(TEST_ID_1);

        // then
        assertSame(categoryDto, actualCategoryDto);
    }

    @Test
    public void getCategoryById_NotFound_Test() {
        // given
        when(categoryRepository.findById(TEST_ID_1)).thenReturn(Optional.empty());

        // when
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> categoryService.getCategory(TEST_ID_1));

        // then
        assertEquals(String.format("Category not found with id : '%s'", TEST_ID_1), exception.getMessage());
    }

    @Test
    public void getAllCategories_Test(@Mock Category category, @Mock CategoryDto categoryDto) {
        // given
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        // when
        List<CategoryDto> actualCategoryList = categoryService.getAllCategories();

        // then
        assertEquals(List.of(categoryDto), actualCategoryList);
    }

    @Test
    public void updateCategoryById_Found_Test(@Mock CategoryDto inputDto, @Mock CategoryDto outputDto,
                                            @Mock Category inputCategory, @Mock Category outputCategory) {
        // given
        when(categoryRepository.save(inputCategory)).thenReturn(outputCategory);
        when(modelMapper.map(outputCategory, CategoryDto.class)).thenReturn(outputDto);

        when(categoryRepository.findById(TEST_ID_1)).thenReturn(Optional.of(inputCategory));
        when(inputDto.getName()).thenReturn(TEST_CATEGORY_NAME);
        when(inputDto.getDescription()).thenReturn(TEST_DESCRIPTION);

        // when
        CategoryDto actualCategoryDto = categoryService.updateCategory(inputDto, TEST_ID_1);

        // then
        assertSame(outputDto, actualCategoryDto);
        verify(inputCategory).setName(TEST_CATEGORY_NAME);
        verify(inputCategory).setDescription(TEST_DESCRIPTION);
    }

    @Test
    public void updateCategoryById_NotFound_Test(@Mock CategoryDto categoryDto) {
        // given
        when(categoryRepository.findById(TEST_ID_1)).thenReturn(Optional.empty());

        // when
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategory(categoryDto, TEST_ID_1));

        // then
        assertEquals(String.format("Category not found with id : '%s'", TEST_ID_1), exception.getMessage());
    }

    @Test
    public void deleteCategoryById_Found_Test(@Mock Category category) {
        // given
        when(categoryRepository.findById(TEST_ID_1)).thenReturn(Optional.of(category));

        // when
        categoryService.deleteCategory(TEST_ID_1);

        // then
        verify(categoryRepository).delete(category);
    }

    @Test
    public void deleteCategoryById_NotFound_Test() {
        // given
        when(categoryRepository.findById(TEST_ID_1)).thenReturn(Optional.empty());

        // when
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(TEST_ID_1));

        // then
        assertEquals(String.format("Category not found with id : '%s'", TEST_ID_1), exception.getMessage());
    }
}
