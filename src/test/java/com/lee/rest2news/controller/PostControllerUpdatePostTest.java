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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerUpdatePostTest {

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private static final long postId = 99L;

    @Test
    void updatePost_Success() throws Exception {
        // given
        PostDto postDto = new PostDto();
        postDto.setId(postId);
        postDto.setTitle("Test title");
        postDto.setDescription("Test description");
        postDto.setContent("Test content");
        postDto.setCategoryId(77L);

        PostDto updatedPostDto = new PostDto();
        updatedPostDto.setId(postId);
        updatedPostDto.setTitle("Updated title");
        updatedPostDto.setDescription("Updated description");
        updatedPostDto.setContent("Updated content");
        updatedPostDto.setCategoryId(22L);

        when(postService.getPostById(postDto.getId())).thenReturn(postDto);
        when(postService.updatePost(updatedPostDto, postId)).thenReturn(updatedPostDto);

        // when
        mockMvc.perform(put("/api/v1/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPostDto)))

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(99)))
                .andExpect(jsonPath("$.title", Matchers.is("Updated title")))
                .andExpect(jsonPath("$.description", Matchers.is("Updated description")))
                .andExpect(jsonPath("$.content", Matchers.is("Updated content")))
                .andExpect(jsonPath("$.categoryId", Matchers.is(22)));
    }

    @Test
    void updatePost_Failed_BecauseTitleLengthLessThan2() throws Exception {
        // given
        PostDto updatedPostDto = new PostDto();
        updatedPostDto.setTitle("1");

        // when
        mockMvc.perform(put("/api/v1/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPostDto)))

                // then
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", Matchers.is("Post title should have at least 2 characters")));
        verifyNoInteractions(postService);
    }

    @Test
    void updatePost_Failed_BecauseDescriptionLengthLessThan10() throws Exception {
        // given
        PostDto updatedPostDto = new PostDto();
        updatedPostDto.setDescription("123456789");

        // when
        mockMvc.perform(put("/api/v1/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPostDto)))

                // then
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description", Matchers.is("Post description should have at least 10 characters")));
        verifyNoInteractions(postService);
    }

    @Test
    void updatePost_Failed_BecauseContentIsEmpty() throws Exception {
        // given
        PostDto updatedPostDto = new PostDto();
        updatedPostDto.setContent(null);

        // when
        mockMvc.perform(put("/api/v1/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPostDto)))

                // then
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content", Matchers.is("must not be empty")));
        verifyNoInteractions(postService);
    }

}
