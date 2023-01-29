package com.lee.rest2news.controller;

import com.lee.rest2news.payload.CommentDto;
import com.lee.rest2news.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.lee.rest2news.TestConstant.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerGetCommentByIdTest {

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getCommentById() throws Exception {
        // given
        long postId = TEST_ID_1;
        long commentId = TEST_ID_2;

        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setUserName(TEST_USER_NAME);
        commentDto.setEmail(TEST_EMAIL);
        commentDto.setTextBody(TEST_TEXT_BODY);
        commentDto.setPostId(postId);

        when(commentService.getCommentById(postId, commentDto.getId())).thenReturn(commentDto);

        // when
        mockMvc.perform(get("/api/v1/posts/{postId}/comments/{id}", postId, commentId))

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.postId", is(1)))
                .andExpect(jsonPath("$.userName", is(TEST_USER_NAME)))
                .andExpect(jsonPath("$.email", is(TEST_EMAIL)))
                .andExpect(jsonPath("$.textBody", is(TEST_TEXT_BODY)));
    }
}
