package com.lee.rest2news.controller;

import com.lee.rest2news.payload.PostDto;
import com.lee.rest2news.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerGetPostsByCategoryTest {

    @MockBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getPostsByCategory() throws Exception {
        // given
        long categoryId = 77L;

        PostDto postDto1 = new PostDto();
        postDto1.setId(101L);
        postDto1.setTitle("101 title");
        postDto1.setDescription("101 description");
        postDto1.setContent("101 content");
        postDto1.setCategoryId(categoryId);

        PostDto postDto2 = new PostDto();
        postDto2.setId(202L);
        postDto2.setTitle("202 title");
        postDto2.setDescription("202 description");
        postDto2.setContent("202 content");
        postDto2.setCategoryId(categoryId);

        when(postService.getPostsByCategory(categoryId)).thenReturn(List.of(postDto1, postDto2));

        // when
        mockMvc.perform(get("/api/v1/posts/category/{id}", 77L))

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(101)))
                .andExpect(jsonPath("$[1].id", is(202)));
    }
}
