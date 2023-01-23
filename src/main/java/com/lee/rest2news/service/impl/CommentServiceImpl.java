package com.lee.rest2news.service.impl;

import com.lee.rest2news.entity.Comment;
import com.lee.rest2news.entity.Post;
import com.lee.rest2news.exception.ApiException;
import com.lee.rest2news.exception.ResourceNotFoundException;
import com.lee.rest2news.payload.CommentDto;
import com.lee.rest2news.repository.CommentRepository;
import com.lee.rest2news.repository.PostRepository;
import com.lee.rest2news.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.lee.rest2news.util.AppConstant.*;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ModelMapper mapper;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, ModelMapper mapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.mapper = mapper;
    }

    @Override
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        Comment comment = mapToEntity(commentDto);
        Post post = getPostById(postId);

        comment.setPost(post);
        Comment newComment = commentRepository.save(comment);

        return mapToDto(newComment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream().map(comment -> mapToDto(comment)).collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long postId, Long commentId) {
        Comment comment = getVerifiedComment(postId, commentId);

        return mapToDto(comment);
    }

    @Override
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentRequest) {
        Comment comment = getVerifiedComment(postId, commentId);

        comment.setUserName(commentRequest.getUserName());
        comment.setEmail(commentRequest.getEmail());
        comment.setTextBody(commentRequest.getTextBody());
        Comment updatedComment = commentRepository.save(comment);

        return mapToDto(updatedComment);
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        Comment comment = getVerifiedComment(postId, commentId);

        commentRepository.delete(comment);
    }

    private CommentDto mapToDto(Comment comment) {
        return mapper.map(comment, CommentDto.class);
    }

    private Comment mapToEntity(CommentDto commentDto) {
        return mapper.map(commentDto, Comment.class);
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException(POST, ID, postId));
    }

    private Comment getVerifiedComment(Long postId, Long commentId) {
        Post post = getPostById(postId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException(COMMENT, ID, commentId));
        if (!comment.getPost().getId().equals(post.getId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Comment doesn't belong to post");
        }
        return comment;
    }
}
