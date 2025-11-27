package dev.charles.SimpleBlogAPI.posts.repository;

import dev.charles.SimpleBlogAPI.posts.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomizedPostsRepository {
    Page<PostDto> findAllByKeyword(boolean isSearchMode, String keyword, Pageable pageable);
    Page<PostDto> findAllByKeywordAndEmail(boolean isSearchMode, String keyword, String email, Pageable pageable);
}
