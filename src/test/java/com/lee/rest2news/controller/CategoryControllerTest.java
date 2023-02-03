package com.lee.rest2news.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.rest2news.payload.CategoryDto;
import com.lee.rest2news.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.lee.rest2news.TestConstant.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addCategory() throws Exception {
        // given
        CategoryDto inputCategoryDto = new CategoryDto();
        inputCategoryDto.setName(TEST_CATEGORY_NAME);
        inputCategoryDto.setDescription(TEST_CATEGORY_DESCRIPTION);

        CategoryDto outputCategoryDto = new CategoryDto();
        outputCategoryDto.setId(10L);
        outputCategoryDto.setName(inputCategoryDto.getName());
        outputCategoryDto.setDescription(inputCategoryDto.getDescription());

        when(categoryService.addCategory(inputCategoryDto)).thenReturn(outputCategoryDto);

        // when
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputCategoryDto)))

                // then
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id", is(10)),
                        jsonPath("$.name", is(TEST_CATEGORY_NAME)),
                        jsonPath("$.description", is(TEST_CATEGORY_DESCRIPTION))
                );
    }


    @Test
    void getCategoryById() throws Exception {
        // given
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(20L);
        categoryDto.setName(TEST_CATEGORY_NAME);
        categoryDto.setDescription(TEST_CATEGORY_DESCRIPTION);

        when(categoryService.getCategory(20L)).thenReturn(categoryDto);

        // when
        mockMvc.perform(get("/api/v1/categories/{id}", 20))

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(20)),
                        jsonPath("$.name", is(TEST_CATEGORY_NAME)),
                        jsonPath("$.description", is(TEST_CATEGORY_DESCRIPTION))
                );
    }

    @Test
    public void getCategories() throws Exception {
        // given
        CategoryDto categoryDto1 = new CategoryDto();
        categoryDto1.setId(30L);
        categoryDto1.setName(TEST_CATEGORY_NAME);
        categoryDto1.setDescription(TEST_CATEGORY_DESCRIPTION);

        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setId(40L);
        categoryDto2.setName(TEST_CATEGORY_NAME);
        categoryDto2.setDescription(TEST_CATEGORY_DESCRIPTION);

        when(categoryService.getAllCategories()).thenReturn(List.of(categoryDto1, categoryDto2));

        // when
        mockMvc.perform(get("/api/v1/categories"))

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", notNullValue()),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].id", is(30)),
                        jsonPath("$[1].id", is(40))
                );
    }

    @Test
    public void updateCategory() throws Exception {
        // given
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(50L);
        categoryDto.setName(TEST_CATEGORY_NAME);
        categoryDto.setDescription(TEST_CATEGORY_DESCRIPTION);

        CategoryDto updatedCategoryDto = new CategoryDto();
        updatedCategoryDto.setId(50L);
        updatedCategoryDto.setName(TEST_CATEGORY_NAME_UPDATED);
        updatedCategoryDto.setDescription(TEST_CATEGORY_DESCRIPTION_UPDATED);

        when(categoryService.getCategory(categoryDto.getId())).thenReturn(categoryDto);
        when(categoryService.updateCategory(updatedCategoryDto, 50L)).thenReturn(updatedCategoryDto);

        // when
        mockMvc.perform(put("/api/v1/categories/{id}", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategoryDto)))

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(50)),
                        jsonPath("$.name", is(TEST_CATEGORY_NAME_UPDATED)),
                        jsonPath("$.description", is(TEST_CATEGORY_DESCRIPTION_UPDATED))
                );
    }

    @Test
    public void deleteCategory() throws Exception {
        // given
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(60L);
        categoryDto.setName(TEST_CATEGORY_NAME);
        categoryDto.setDescription(TEST_CATEGORY_DESCRIPTION);

        when(categoryService.getCategory(60L)).thenReturn(categoryDto);

        // when
        mockMvc.perform(delete("/api/v1/categories/{id}", 60))

                // then
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}
