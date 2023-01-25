package com.lee.rest2news.controller;

import com.lee.rest2news.payload.PostDto;
import com.lee.rest2news.payload.PostResponse;
import com.lee.rest2news.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerGetAllPostsTest {

    @MockBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllPosts_With_NonDefaultParams() throws Exception {
        // given
        PostResponse postResponse = createTestResponse();

        when(postService.getAllPosts(100, 200, "test", "desc")).thenReturn(postResponse);

        // when
        mockMvc.perform(get("/api/v1/posts")
                        .param("pageNo", "100")
                        .param("pageSize", "200")
                        .param("sortBy", "test")
                        .param("sortDir", "desc"))

                // then
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content[0].id", is(33)))
                .andExpect(jsonPath("$.content[0].title", is("Title")))
                .andExpect(jsonPath("$.content[0].description", is("Description")))
                .andExpect(jsonPath("$.content[0].content", is("Content")))
                .andExpect(jsonPath("$.content[0].categoryId", is(44)))

                .andExpect(jsonPath("$.pageNo", is(666)))
                .andExpect(jsonPath("$.pageSize", is(777)))
                .andExpect(jsonPath("$.totalElements", is(888)))
                .andExpect(jsonPath("$.totalPages", is(999)))
                .andExpect(jsonPath("$.last", is(true)));
    }

    @Test
    void getAllPosts_Without_Params() throws Exception {
        // given
        PostResponse postResponse = createTestResponse();

        when(postService.getAllPosts(0, 5, "id", "asc")).thenReturn(postResponse);

        // when
        mockMvc.perform(get("/api/v1/posts"))

                // then
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content[0].id", is(33)))
                .andExpect(jsonPath("$.content[0].title", is("Title")))
                .andExpect(jsonPath("$.content[0].description", is("Description")))
                .andExpect(jsonPath("$.content[0].content", is("Content")))
                .andExpect(jsonPath("$.content[0].categoryId", is(44)))

                .andExpect(jsonPath("$.pageNo", is(666)))
                .andExpect(jsonPath("$.pageSize", is(777)))
                .andExpect(jsonPath("$.totalElements", is(888)))
                .andExpect(jsonPath("$.totalPages", is(999)))
                .andExpect(jsonPath("$.last", is(true)));
    }

    private static PostResponse createTestResponse() {
        PostDto postDto = new PostDto();
        postDto.setId(33L);
        postDto.setTitle("Title");
        postDto.setDescription("Description");
        postDto.setContent("Content");
        postDto.setCategoryId(44L);

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(List.of(postDto));
        postResponse.setLast(true);
        postResponse.setPageNo(666);
        postResponse.setPageSize(777);
        postResponse.setTotalElements(888);
        postResponse.setTotalPages(999);

        return postResponse;
    }
}
