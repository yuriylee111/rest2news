package com.lee.rest2news.service;

import com.lee.rest2news.entity.Category;
import com.lee.rest2news.entity.Post;
import com.lee.rest2news.exception.ResourceNotFoundException;
import com.lee.rest2news.payload.PostDto;
import com.lee.rest2news.payload.PostResponse;
import com.lee.rest2news.repository.CategoryRepository;
import com.lee.rest2news.repository.PostRepository;
import com.lee.rest2news.service.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static com.lee.rest2news.TestConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    public void createPostTest(@Mock PostDto inputDto, @Mock PostDto outputDto,
                               @Mock Post inputPost, @Mock Post outputPost) {
        // given
        when(modelMapper.map(inputDto, Post.class)).thenReturn(inputPost);
        when(postRepository.save(inputPost)).thenReturn(outputPost);
        when(modelMapper.map(outputPost, PostDto.class)).thenReturn(outputDto);

        // when
        PostDto resultDto = postService.createPost(inputDto);

        // then
        assertSame(outputDto, resultDto);
    }

    @Test
    public void getAllPosts_ASC_Test(@Mock Page<Post> page, @Mock Post post, @Mock PostDto postDto) {
        // given
        when(postRepository.findAll(PageRequest.of(TEST_PAGE_NUMBER, TEST_PAGE_SIZE, Sort.by(TEST_SORT_BY).ascending())))
                .thenReturn(page);
        when(page.getContent()).thenReturn(List.of(post));
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);
        when(page.getNumber()).thenReturn(TEST_PAGE_NUMBER);
        when(page.getSize()).thenReturn(TEST_PAGE_SIZE);
        when(page.getTotalElements()).thenReturn(TEST_TOTAL_ELEMENTS);
        when(page.getTotalPages()).thenReturn(TEST_TOTAL_PAGES);
        when(page.isLast()).thenReturn(true);

        // when
        PostResponse allPosts = postService.getAllPosts(TEST_PAGE_NUMBER, TEST_PAGE_SIZE, TEST_SORT_BY, TEST_SORT_DIRECTION_ASC);

        // then
        assertEquals(
                PostResponse.builder()
                        .last(true)
                        .pageNo(TEST_PAGE_NUMBER)
                        .pageSize(TEST_PAGE_SIZE)
                        .totalElements(TEST_TOTAL_ELEMENTS)
                        .totalPages(TEST_TOTAL_PAGES)
                        .content(List.of(postDto))
                        .build(),
                allPosts
        );
    }

    @Test
    public void getAllPosts_DESC_Test(@Mock Page<Post> page, @Mock Post post, @Mock PostDto postDto) {
        // given
        when(postRepository.findAll(PageRequest.of(TEST_PAGE_NUMBER, TEST_PAGE_SIZE, Sort.by(TEST_SORT_BY).descending())))
                .thenReturn(page);
        when(page.getContent()).thenReturn(List.of(post));
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);
        when(page.getNumber()).thenReturn(TEST_PAGE_NUMBER);
        when(page.getSize()).thenReturn(TEST_PAGE_SIZE);
        when(page.getTotalElements()).thenReturn(TEST_TOTAL_ELEMENTS);
        when(page.getTotalPages()).thenReturn(TEST_TOTAL_PAGES);
        when(page.isLast()).thenReturn(true);

        // when
        PostResponse allPosts = postService.getAllPosts(TEST_PAGE_NUMBER, TEST_PAGE_SIZE, TEST_SORT_BY, TEST_SORT_DIRECTION_DESC);

        // then
        assertEquals(
                PostResponse.builder()
                        .last(true)
                        .pageNo(TEST_PAGE_NUMBER)
                        .pageSize(TEST_PAGE_SIZE)
                        .totalElements(TEST_TOTAL_ELEMENTS)
                        .totalPages(TEST_TOTAL_PAGES)
                        .content(List.of(postDto))
                        .build(),
                allPosts
        );
    }

    @Test
    public void getPostById_Found_Test(@Mock Post post, @Mock PostDto postDto) {
        // given
        when(postRepository.findById(TEST_ID_1)).thenReturn(Optional.of(post));
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        // when
        PostDto postById = postService.getPostById(TEST_ID_1);

        // then
        assertSame(postDto, postById);
    }

    @Test
    public void getPostById_NotFound_Test() {
        // given
        when(postRepository.findById(TEST_ID_1)).thenReturn(Optional.empty());

        // when
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> postService.getPostById(TEST_ID_1));

        // then
        assertEquals(String.format("Post not found with id : '%s'", TEST_ID_1), exception.getMessage());
    }

    @Test
    public void updatePost_Success_Test(@Mock PostDto inputDto, @Mock PostDto outputDto,
                                        @Mock Post inputPost, @Mock Post outputPost) {
        // given
        when(postRepository.save(inputPost)).thenReturn(outputPost);
        when(modelMapper.map(outputPost, PostDto.class)).thenReturn(outputDto);

        when(postRepository.findById(TEST_ID_1)).thenReturn(Optional.of(inputPost));

        when(inputDto.getTitle()).thenReturn(TEST_TITLE_1);
        when(inputDto.getDescription()).thenReturn(TEST_DESCRIPTION_UPDATED);
        when(inputDto.getContent()).thenReturn(TEST_CONTENT_UPDATED);

        // when
        PostDto postDto = postService.updatePost(inputDto, TEST_ID_1);

        // then
        assertSame(outputDto, postDto);  // на 1 месте - ожидаемый, на 2 - реальный
        verify(inputPost).setTitle(TEST_TITLE_1);
        verify(inputPost).setDescription(TEST_DESCRIPTION_UPDATED);
        verify(inputPost).setContent(TEST_CONTENT_UPDATED);
    }

    @Test
    public void updatePost_PostNotFound_Test(@Mock PostDto postDto) {
        // given
        when(postRepository.findById(TEST_ID_1)).thenReturn(Optional.empty());

        // when
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> postService.updatePost(postDto, TEST_ID_1));

        // then
        assertEquals(String.format("Post not found with id : '%s'", TEST_ID_1), exception.getMessage());
    }


    @Test
    public void deletePostById_Success_Test(@Mock Post post) {
        // given
        when(postRepository.findById(TEST_ID_1)).thenReturn(Optional.of(post));

        // when
        postService.deletePostById(TEST_ID_1);

        // then
        verify(postRepository).delete(post);
    }

    @Test
    public void deletePostById_PostNotFound_Test() {
        // given
        when(postRepository.findById(TEST_ID_1)).thenReturn(Optional.empty());

        // when
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> postService.deletePostById(TEST_ID_1));

        // then
        assertEquals(String.format("Post not found with id : '%s'", TEST_ID_1), exception.getMessage());
    }


    @Test
    public void getPostsByCategory_Found_Test(@Mock Category category, @Mock Post post, @Mock PostDto postDto) {
        // given
        when(categoryRepository.findById(TEST_ID_1)).thenReturn(Optional.of(category));
        when(postRepository.findByCategoryId(TEST_ID_1)).thenReturn(List.of(post));
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        // when
        List<PostDto> postsByCategory = postService.getPostsByCategory(TEST_ID_1);

        // then
        assertEquals(List.of(postDto), postsByCategory); // не мок, а объект
    }

    @Test
    public void getPostsByCategory_NotFound_Test() {
        // given
        when(categoryRepository.findById(TEST_ID_1)).thenReturn(Optional.empty());

        // when
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> postService.getPostsByCategory(TEST_ID_1));

        // then
        assertEquals(String.format("Category not found with id : '%s'", TEST_ID_1), exception.getMessage());
    }

}
