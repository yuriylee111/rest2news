package com.lee.rest2news.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private Long id;

    @Size(min = 2, message = "Post title should have at least 2 characters")
    private String title;

    @Size(min = 10, message = "Post description should have at least 10 characters")
    private String description;

    @NotEmpty
    private String content;

    private Set<CommentDto> comments;

    private Long categoryId;

}
