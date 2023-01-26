package com.lee.rest2news.controller;

import com.lee.rest2news.payload.PostDto;
import com.lee.rest2news.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerDeletePostTest {

    @MockBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void deletePost() throws Exception {
        // given
        PostDto postDto = new PostDto();
        postDto.setId(123L);
        postDto.setTitle("Test title");
        postDto.setDescription("Test description");
        postDto.setContent("Test content");
        postDto.setCategoryId(789L);

        when(postService.getPostById(123L)).thenReturn(postDto);

        // when
        mockMvc.perform(delete("/api/v1/posts/{id}", 123))

                // then
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
