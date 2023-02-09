package com.lee.rest2news.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.rest2news.entity.Category;
import com.lee.rest2news.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static com.lee.rest2news.TestConstant.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class CategoryControllerIT {

    @Container
    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer("mysql:latest");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        categoryRepository.deleteAll();
    }

    @Test
    void addCategory() throws Exception {
        // given
        Category inputCategory = new Category();
        inputCategory.setName(TEST_CATEGORY_NAME);
        inputCategory.setDescription(TEST_CATEGORY_DESCRIPTION);

        // when
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputCategory)))

                // then
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.name", is(TEST_CATEGORY_NAME)),
                        jsonPath("$.description", is(TEST_CATEGORY_DESCRIPTION))
                );
    }

    @Test
    public void getCategories() throws Exception {
        // given
        Category category1 = new Category();
        category1.setName(TEST_CATEGORY_NAME);
        category1.setDescription(TEST_CATEGORY_DESCRIPTION);

        Category category2 = new Category();
        category2.setName(TEST_CATEGORY_NAME);
        category2.setDescription(TEST_CATEGORY_DESCRIPTION);

        List<Category> listOfCategories = new ArrayList<>();
        listOfCategories.add(category1);
        listOfCategories.add(category2);

        categoryRepository.saveAll(listOfCategories);

        // when
        mockMvc.perform(get("/api/v1/categories"))

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", notNullValue()),
                        jsonPath("$", hasSize(2))
                );
    }

    @Test
    void getCategoryById() throws Exception {
        // given
        Category category = new Category();
        category.setName(TEST_CATEGORY_NAME);
        category.setDescription(TEST_CATEGORY_DESCRIPTION);

        categoryRepository.save(category);

        // when
        mockMvc.perform(get("/api/v1/categories/{id}", category.getId()))

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name", is(TEST_CATEGORY_NAME)),
                        jsonPath("$.description", is(TEST_CATEGORY_DESCRIPTION))
                );
    }

    @Test
    void getCategoryById_NotFound() throws Exception {
        // given
        Long categoryId = 1L;
        Category category = new Category();
        category.setName(TEST_CATEGORY_NAME);
        category.setDescription(TEST_CATEGORY_DESCRIPTION);

        categoryRepository.save(category);

        // when
        mockMvc.perform(get("/api/v1/categories/{id}", categoryId))

                // then
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateCategory() throws Exception {
        // given
        Category savedCategory = new Category();
        savedCategory.setName(TEST_CATEGORY_NAME);
        savedCategory.setDescription(TEST_CATEGORY_DESCRIPTION);

        categoryRepository.save(savedCategory);

        Category updatedCategory = new Category();
        updatedCategory.setName(TEST_CATEGORY_NAME_UPDATED);
        updatedCategory.setDescription(TEST_CATEGORY_DESCRIPTION_UPDATED);

        // when
        mockMvc.perform(put("/api/v1/categories/{id}", savedCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory)))

                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name", is(TEST_CATEGORY_NAME_UPDATED)),
                        jsonPath("$.description", is(TEST_CATEGORY_DESCRIPTION_UPDATED))
                );
    }

    @Test
    public void updateCategory_NotFound() throws Exception {
        // given
        Long categoryId = 1L;
        Category savedCategory = new Category();
        savedCategory.setName(TEST_CATEGORY_NAME);
        savedCategory.setDescription(TEST_CATEGORY_DESCRIPTION);

        categoryRepository.save(savedCategory);

        Category updatedCategory = new Category();
        updatedCategory.setName(TEST_CATEGORY_NAME_UPDATED);
        updatedCategory.setDescription(TEST_CATEGORY_DESCRIPTION_UPDATED);

        // when
        mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory)))

                // then
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteCategory() throws Exception {
        // given
        Category savedCategory = new Category();
        savedCategory.setName(TEST_CATEGORY_NAME);
        savedCategory.setDescription(TEST_CATEGORY_DESCRIPTION);

        categoryRepository.save(savedCategory);

        // when
        mockMvc.perform(delete("/api/v1/categories/{id}", savedCategory.getId()))

                // then
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
