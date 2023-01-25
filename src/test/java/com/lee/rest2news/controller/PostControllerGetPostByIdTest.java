package com.lee.rest2news.controller;

import com.lee.rest2news.payload.PostDto;
import com.lee.rest2news.service.PostService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerGetPostByIdTest {

    @MockBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getPostByIdV1() throws Exception {
        // given
        PostDto postDto = new PostDto();
        postDto.setId(12L);
        postDto.setTitle("Test title V1");
        postDto.setDescription("Test description V1");
        postDto.setContent("Test content V1");
        postDto.setCategoryId(34L);

        when(postService.getPostById(12L)).thenReturn(postDto);

        // when
        mockMvc.perform(get("/api/v1/posts/{id}", 12))

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(12)))
                .andExpect(jsonPath("$.title", Matchers.is("Test title V1")))
                .andExpect(jsonPath("$.description", Matchers.is("Test description V1")))
                .andExpect(jsonPath("$.content", Matchers.is("Test content V1")))
                .andExpect(jsonPath("$.categoryId", Matchers.is(34)));
    }

    @Test
    public void getPostByIdV2() throws Exception {
        // given
        PostDto postDto = new PostDto();
        postDto.setId(45L);
        postDto.setTitle("Test title V1");
        postDto.setDescription("Test description V1");
        postDto.setContent("Test content V1");
        postDto.setCategoryId(34L);

        when(postService.getPostById(45L)).thenReturn(postDto);

        // when
        mockMvc.perform(get("/api/v2/posts/{id}", 45))

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(45)))
                .andExpect(jsonPath("$.title", Matchers.is("Test title V1")))
                .andExpect(jsonPath("$.description", Matchers.is("Test description V1")))
                .andExpect(jsonPath("$.content", Matchers.is("Test content V1")))
                .andExpect(jsonPath("$.tags[0]", Matchers.is("Java")))
                .andExpect(jsonPath("$.tags[1]", Matchers.is("Spring Boot")))
                .andExpect(jsonPath("$.tags[2]", Matchers.is("AWS")));

    }
}
