package com.lee.rest2news.controller;

import com.lee.rest2news.payload.CommentDto;
import com.lee.rest2news.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable(value = "postId") Long postId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return commentService.createComment(postId, commentDto);
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentDto> getCommentsByPostId(@PathVariable(value = "postId") Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @GetMapping("/posts/{postId}/comments/{id}")
    public CommentDto getCommentById(@PathVariable(value = "postId") Long postId,
                                     @PathVariable(value = "id") Long commentId) {
        return commentService.getCommentById(postId, commentId);
    }

    @PutMapping("/posts/{postId}/comments/{id}")
    public CommentDto updateComment(@PathVariable(value = "postId") Long postId,
                                    @PathVariable(value = "id") Long commentId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return commentService.updateComment(postId, commentId, commentDto);
    }

    @DeleteMapping("/posts/{postId}/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(value = "postId") Long postId,
                              @PathVariable(value = "id") Long commentId) {
        commentService.deleteComment(postId, commentId);
    }
}