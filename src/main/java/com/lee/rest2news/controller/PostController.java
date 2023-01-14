package com.lee.rest2news.controller;

import com.lee.rest2news.payload.PostDto;
import com.lee.rest2news.payload.PostDtoV2;
import com.lee.rest2news.payload.PostResponse;
import com.lee.rest2news.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static com.lee.rest2news.util.AppConstant.*;

@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/v1/posts")
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto) {
        return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
    }

    @GetMapping("/v1/posts")
    public PostResponse getAllPosts(
            @RequestParam(value = PAGE_NO_TEMPLATE, defaultValue = DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = PAGE_SIZE_TEMPLATE, defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = SORT_BY_TEMPLATE, defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = SORT_DIR_TEMPLATE, defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        return postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);
    }

    @GetMapping("/v1/posts/{id}")
    public ResponseEntity<PostDto> getPostByIdV1(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/v2/posts/{id}")
    public ResponseEntity<PostDtoV2> getPostByIdV2(@PathVariable(value = "id") Long id) {
        PostDto postDto = postService.getPostById(id);
        PostDtoV2 postDtoV2 = new PostDtoV2();
        postDtoV2.setId(postDto.getId());
        postDtoV2.setTitle(postDto.getTitle());
        postDtoV2.setDescription(postDto.getDescription());
        postDtoV2.setContent(postDto.getContent());

        List<String> tags = new ArrayList<>();
        tags.add("Java");
        tags.add("Spring Boot");
        tags.add("AWS");
        postDtoV2.setTags(tags);

        return ResponseEntity.ok(postDtoV2);
    }

    @PutMapping("/v1/posts/{id}")
    public ResponseEntity<PostDto> updatePost(@Valid @RequestBody PostDto postDto, @PathVariable(value = "id") Long id) {
        PostDto postResponse = postService.updatePost(postDto, id);
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    @DeleteMapping("/v1/posts/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(value = "id") Long id) {
        postService.deletePostById(id);
        return new ResponseEntity<>("Post entity deleted successfully.", HttpStatus.NO_CONTENT);
    }
}
