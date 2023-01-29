package com.lee.rest2news.payload;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class CommentDto {

    private Long id;

    @NotEmpty(message = "Name should not be null or empty")
    private String userName;

    @NotEmpty(message = "Email should not be null or empty")
    @Email(message = "Email is not valid")
    private String email;

    @Size(min = 10, message = "Comment body must be minimum 10 characters")
    private String textBody;

    private Long postId;
}
