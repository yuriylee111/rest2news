package com.lee.rest2news.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.rest2news.payload.PostDto;
import com.lee.rest2news.service.PostService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerCreatePostTest {

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createPost_Success() throws Exception {
        // given
        PostDto inputPostDto = createTestPostDto();

        PostDto outputPostDto = createTestPostDto();
        outputPostDto.setId(22L);

        when(postService.createPost(inputPostDto)).thenReturn(outputPostDto);

        // when
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputPostDto)))

                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.is(22)))
                .andExpect(jsonPath("$.title", Matchers.is("Test title")))
                .andExpect(jsonPath("$.description", Matchers.is("Test description")))
                .andExpect(jsonPath("$.content", Matchers.is("Test content")))
                .andExpect(jsonPath("$.categoryId", Matchers.is(333)));
    }

    @Test
    void createPost_Failed_BecauseTitleLengthLessThan2() throws Exception {
        // given
        PostDto inputPostDto = createTestPostDto();
        inputPostDto.setTitle("1");

        // when
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputPostDto)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", Matchers.is("Post title should have at least 2 characters")));
        verifyNoInteractions(postService);
    }

    @Test
    void createPost_Failed_BecauseDescLengthLessThan10() throws Exception {
        // given
        PostDto inputPostDto = createTestPostDto();
        inputPostDto.setDescription("123456789");

        // when
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputPostDto)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description", Matchers.is("Post description should have at least 10 characters")));
        verifyNoInteractions(postService);
    }

    @Test
    void createPost_Failed_BecauseContentIsEmpty() throws Exception {
        // given
        PostDto inputPostDto = createTestPostDto();
        inputPostDto.setContent(null);

        // when
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputPostDto)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content", Matchers.is("must not be empty")));
        verifyNoInteractions(postService);
    }

    private static PostDto createTestPostDto() {
        PostDto inputPostDto = new PostDto();
        inputPostDto.setTitle("Test title");
        inputPostDto.setDescription("Test description");
        inputPostDto.setContent("Test content");
        inputPostDto.setCategoryId(333L);
        return inputPostDto;
    }
}
