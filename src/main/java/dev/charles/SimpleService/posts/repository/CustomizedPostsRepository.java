package dev.charles.SimpleService.posts.repository;

import dev.charles.SimpleService.posts.dto.PostDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomizedPostsRepository {
    List<PostDto> findAllByKeyword(String keyword, Pageable pageable);
    List<PostDto> findAllByKeywordAndEmail(String keyword, String email, Pageable pageable);
    Long countByKeyword(String keyword);
    Long countByKeywordAndEmail(String keyword,String email);
}
