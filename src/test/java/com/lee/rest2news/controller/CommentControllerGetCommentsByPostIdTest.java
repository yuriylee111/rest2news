package com.lee.rest2news.controller;

import com.lee.rest2news.payload.CommentDto;
import com.lee.rest2news.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.lee.rest2news.TestConstant.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerGetCommentsByPostIdTest {

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getCommentsByPostId() throws Exception {
        // given
        long postId = 45L;

        CommentDto commentDto1 = new CommentDto();
        commentDto1.setUserName(TEST_USER_NAME_1);
        commentDto1.setEmail(TEST_EMAIL_1);
        commentDto1.setTextBody(TEST_TEXT_BODY_1);
        commentDto1.setPostId(postId);

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setUserName(TEST_USER_NAME_2);
        commentDto2.setEmail(TEST_EMAIL_2);
        commentDto2.setTextBody(TEST_TEXT_BODY_2);
        commentDto2.setPostId(postId);

        when(commentService.getCommentsByPostId(postId)).thenReturn(List.of(commentDto1, commentDto2));

        // when
        mockMvc.perform(get("/api/v1/posts/{postId}/comments", postId))

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].postId", is(45)))
                .andExpect(jsonPath("$[1].postId", is(45)))
                .andExpect(jsonPath("$[0].userName", is(TEST_USER_NAME_1)))
                .andExpect(jsonPath("$[1].userName", is(TEST_USER_NAME_2)));
    }
}
