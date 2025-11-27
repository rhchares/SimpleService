package dev.charles.SimpleBlogAPI.posts.repository;

import dev.charles.SimpleBlogAPI.posts.domain.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostsRepository extends JpaRepository<Posts, Long>, CustomizedPostsRepository{
    <T> Optional<T> findById(Long id, Class<T> type);


}
