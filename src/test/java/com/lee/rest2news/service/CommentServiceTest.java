package com.lee.rest2news.service;

import com.lee.rest2news.entity.Comment;
import com.lee.rest2news.entity.Post;
import com.lee.rest2news.exception.ResourceNotFoundException;
import com.lee.rest2news.payload.CommentDto;
import com.lee.rest2news.repository.CommentRepository;
import com.lee.rest2news.repository.PostRepository;
import com.lee.rest2news.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static com.lee.rest2news.TestConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    public void createComment_Test(@Mock Post post, @Mock Comment comment, @Mock CommentDto commentDto) {
        // given
        when(modelMapper.map(commentDto, Comment.class)).thenReturn(comment);
        when(postRepository.findById(TEST_ID_1)).thenReturn(Optional.of(post));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(modelMapper.map(comment, CommentDto.class)).thenReturn(commentDto);

        // when
        CommentDto actualComment = commentService.createComment(TEST_ID_1, commentDto);

        // then
        assertEquals(commentDto, actualComment);
        verify(comment).setPost(post);
    }

    @Test
    public void getCommentsByPostId_Test(@Mock Comment comment, @Mock CommentDto commentDto) {
        // given
        when(commentRepository.findByPostId(TEST_ID_1)).thenReturn(List.of(comment));
        when(modelMapper.map(comment, CommentDto.class)).thenReturn(commentDto);

        // when
        List<CommentDto> actualCommentsList = commentService.getCommentsByPostId(TEST_ID_1);

        // then
        assertEquals(List.of(commentDto), actualCommentsList);
    }

    @Test
    public void getCommentById_Test(@Mock Post post, @Mock Comment comment, @Mock CommentDto commentDto) {
        // given
        when(postRepository.findById(TEST_ID_1)).thenReturn(Optional.of(post));
        when(commentRepository.findById(TEST_ID_1)).thenReturn(Optional.of(comment));
        when(comment.getPost()).thenReturn(post);
        when(comment.getPost().getId()).thenReturn(TEST_ID_1);
        when(modelMapper.map(comment, CommentDto.class)).thenReturn(commentDto);

        // when
        CommentDto actualComment = commentService.getCommentById(TEST_ID_1, TEST_ID_1);

        // then
        assertSame(commentDto, actualComment);
    }

    @Test
    public void getCommentById_PostNotFound_Test() {
        // given
        when(postRepository.findById(TEST_ID_1)).thenReturn(Optional.empty());

        // when
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.getCommentById(TEST_ID_1, TEST_ID_1));

        // then
        assertEquals(String.format("Post not found with id : '%s'", TEST_ID_1), exception.getMessage());
    }

    @Test
    public void getCommentById_CommentNotFound_Test(@Mock Post post) {
        // given
        when(postRepository.findById(TEST_ID_1)).thenReturn(Optional.of(post));
        when(commentRepository.findById(TEST_ID_1)).thenReturn(Optional.empty());

        // when
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.getCommentById(TEST_ID_1, TEST_ID_1));

        // then
        assertEquals(String.format("Comment not found with id : '%s'", TEST_ID_1), exception.getMessage());
    }

    @Test
    public void updateComment_Test(@Mock Post post,
                                   @Mock Comment inputComment, @Mock CommentDto inputCommentDto,
                                   @Mock Comment outputComment, @Mock CommentDto outputCommentDto) {
        // given
        when(postRepository.findById(TEST_ID_1)).thenReturn(Optional.of(post));
        when(inputComment.getPost()).thenReturn(post);
        when(commentRepository.save(inputComment)).thenReturn(outputComment);
        when(modelMapper.map(outputComment, CommentDto.class)).thenReturn(outputCommentDto);

        when(commentRepository.findById(TEST_ID_1)).thenReturn(Optional.of(inputComment));

        when(inputCommentDto.getUserName()).thenReturn(TEST_USER_NAME);
        when(inputCommentDto.getEmail()).thenReturn(TEST_EMAIL);
        when(inputCommentDto.getTextBody()).thenReturn(TEST_TEXT_BODY);

        // when
        CommentDto updatedCommentDto = commentService.updateComment(TEST_ID_1, TEST_ID_1, inputCommentDto);

        // then
        assertSame(outputCommentDto, updatedCommentDto);
        verify(inputComment).setUserName(TEST_USER_NAME);
        verify(inputComment).setEmail(TEST_EMAIL);
        verify(inputComment).setTextBody(TEST_TEXT_BODY);
    }

    @Test
    public void updateComment_CommentNotFound_Test(@Mock Post post, @Mock CommentDto commentDto) {
        // given
        when(postRepository.findById(TEST_ID_1)).thenReturn(Optional.of(post));
        when(commentRepository.findById(TEST_ID_1)).thenReturn(Optional.empty());

        // when
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.updateComment(TEST_ID_1, TEST_ID_1, commentDto));

        // then
        assertEquals(String.format("Comment not found with id : '%s'", TEST_ID_1), exception.getMessage());
    }

    @Test
    public void deleteComment_Test(@Mock Post post, @Mock Comment comment) {
        // given
        when(postRepository.findById(TEST_ID_1)).thenReturn(Optional.of(post));
        when(commentRepository.findById(TEST_ID_1)).thenReturn(Optional.of(comment));
        when(comment.getPost()).thenReturn(post);
        when(comment.getPost().getId()).thenReturn(TEST_ID_1);
        // when
        commentService.deleteComment(TEST_ID_1, TEST_ID_1);

        // then
        verify(commentRepository).delete(comment);
    }

    @Test
    public void deleteComment_CommentNotFound_Test(@Mock Post post) {
        // given
        when(postRepository.findById(TEST_ID_1)).thenReturn(Optional.of(post));
        when(commentRepository.findById(TEST_ID_1)).thenReturn(Optional.empty());

        // when
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.deleteComment(TEST_ID_1, TEST_ID_1));

        // then
        assertEquals(String.format("Comment not found with id : '%s'", TEST_ID_1), exception.getMessage());
    }
}
