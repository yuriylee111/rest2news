package com.lee.rest2news.repository;

import com.lee.rest2news.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
