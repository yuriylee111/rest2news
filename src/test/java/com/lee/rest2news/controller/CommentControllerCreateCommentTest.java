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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerCreateCommentTest {

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createComment_Success() throws Exception {
        // given
        CommentDto inputCommentDto = createTestCommentDto();

        CommentDto outputCommentDto = createTestCommentDto();
        outputCommentDto.setId(22L);

        when(commentService.createComment(TEST_ID_1, inputCommentDto)).thenReturn(outputCommentDto);

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments", TEST_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputCommentDto)))

                // then
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.is(22)))
                .andExpect(jsonPath("$.userName", Matchers.is(TEST_USER_NAME)))
                .andExpect(jsonPath("$.email", Matchers.is(TEST_EMAIL)))
                .andExpect(jsonPath("$.textBody", Matchers.is(TEST_TEXT_BODY)));
    }

    @Test
    void createComment_Failed_BecauseUserNameIsEmpty() throws Exception {
        // given
        CommentDto inputCommentDto = createTestCommentDto();
        inputCommentDto.setUserName(null);

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments", TEST_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputCommentDto)))

                // then
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userName", Matchers.is("Name should not be null or empty")));
        verifyNoInteractions(commentService);
    }

    @Test
    void createComment_Failed_BecauseEmailIsEmpty() throws Exception {
        // given
        CommentDto inputCommentDto = createTestCommentDto();
        inputCommentDto.setEmail(null);

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments", TEST_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputCommentDto)))

                // then
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email", Matchers.is("Email should not be null or empty")));
        verifyNoInteractions(commentService);
    }

    @Test
    void createComment_Failed_BecauseEmailNotValid() throws Exception {
        // given
        CommentDto inputCommentDto = createTestCommentDto();
        inputCommentDto.setEmail(TEST_EMAIL_NOT_VALID);

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments", TEST_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputCommentDto)))

                // then
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email", Matchers.is("Email is not valid")));
        verifyNoInteractions(commentService);
    }

    @Test
    void createComment_Failed_BecauseNextBodyLengthLessThan10() throws Exception {
        // given
        CommentDto inputCommentDto = createTestCommentDto();
        inputCommentDto.setTextBody("123456789");

        // when
        mockMvc.perform(post("/api/v1/posts/{postId}/comments", TEST_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputCommentDto)))

                // then
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.textBody", Matchers.is("Comment body must be minimum 10 characters")));
        verifyNoInteractions(commentService);
    }


    private static CommentDto createTestCommentDto() {
        CommentDto inputCommentDto = new CommentDto();
        inputCommentDto.setUserName(TEST_USER_NAME);
        inputCommentDto.setEmail(TEST_EMAIL);
        inputCommentDto.setTextBody(TEST_TEXT_BODY);
        return inputCommentDto;
    }

}
