package com.lee.rest2news.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.rest2news.payload.CommentDto;
import com.lee.rest2news.service.CommentService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.lee.rest2news.TestConstant.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerUpdateCommentTest {

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private static final long commentId = 79L;

    @Test
    public void updateComment_Success() throws Exception {
        // given
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setUserName(TEST_USER_NAME);
        commentDto.setEmail(TEST_EMAIL);
        commentDto.setTextBody(TEST_TEXT_BODY);

        CommentDto updatedCommentDto = new CommentDto();
        updatedCommentDto.setId(commentId);
        updatedCommentDto.setUserName(TEST_USER_NAME_UPDATED);
        updatedCommentDto.setEmail(TEST_EMAIL_UPDATED);
        updatedCommentDto.setTextBody(TEST_TEXT_BODY_UPDATED);

        when(commentService.getCommentById(TEST_ID_1, commentDto.getId())).thenReturn(commentDto);
        when(commentService.updateComment(TEST_ID_1, commentDto.getId(), updatedCommentDto)).thenReturn(updatedCommentDto);

        // when
        mockMvc.perform(put("/api/v1/posts/{postId}/comments/{id}", TEST_ID_1, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommentDto)))
                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName", is(TEST_USER_NAME_UPDATED)))
                .andExpect(jsonPath("$.email", is(TEST_EMAIL_UPDATED)))
                .andExpect(jsonPath("$.textBody", is(TEST_TEXT_BODY_UPDATED)));
    }

    @Test
    public void updateComment_Failed_BecauseUserNameIsEmpty() throws Exception {
        // given
        CommentDto updatedCommentDto = new CommentDto();
        updatedCommentDto.setUserName(null);

        // when
        mockMvc.perform(put("/api/v1/posts/{postId}/comments/{id}", TEST_ID_1, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommentDto)))

                // then
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userName", Matchers.is("Name should not be null or empty")));
        verifyNoInteractions(commentService);
    }

    @Test
    public void updateComment_Failed_BecauseEmailIsEmpty() throws Exception {
        // given
        CommentDto updatedCommentDto = new CommentDto();
        updatedCommentDto.setEmail(null);

        // when
        mockMvc.perform(put("/api/v1/posts/{postId}/comments/{id}", TEST_ID_1, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommentDto)))

                // then
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email", Matchers.is("Email should not be null or empty")));
        verifyNoInteractions(commentService);
    }

    @Test
    public void updateComment_Failed_BecauseEmailNotValid() throws Exception {
        // given
        CommentDto updatedCommentDto = new CommentDto();
        updatedCommentDto.setEmail(TEST_EMAIL_NOT_VALID);

        // when
        mockMvc.perform(put("/api/v1/posts/{postId}/comments/{id}", TEST_ID_1, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommentDto)))

                // then
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email", Matchers.is("Email is not valid")));
        verifyNoInteractions(commentService);
    }

    @Test
    void updateComment_Failed_BecauseNextBodyLengthLessThan10() throws Exception {
        // given
        CommentDto updatedCommentDto = new CommentDto();
        updatedCommentDto.setTextBody("123456789");

        // when
        mockMvc.perform(put("/api/v1/posts/{postId}/comments/{id}", TEST_ID_1, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommentDto)))

                // then
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.textBody", Matchers.is("Comment body must be minimum 10 characters")));
        verifyNoInteractions(commentService);
    }
}
