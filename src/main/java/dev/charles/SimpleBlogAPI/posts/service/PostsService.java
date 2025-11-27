package dev.charles.SimpleBlogAPI.posts.service;

import dev.charles.SimpleBlogAPI.errors.exception.NotFoundResourceException;
import dev.charles.SimpleBlogAPI.posts.domain.Posts;
import dev.charles.SimpleBlogAPI.posts.dto.PostDto;
import dev.charles.SimpleBlogAPI.posts.repository.PostsRepository;
import dev.charles.SimpleBlogAPI.users.domain.Users;

import dev.charles.SimpleBlogAPI.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostsService {
    final private PostsRepository postsRepository;
    final private UsersRepository usersRepository;

    @Transactional
    public void createPost(String email, PostDto postDto) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundResourceException("Not found user by email"));
        Posts post = Posts.of(postDto);
        post.setUser(user);
        postsRepository.save(post);
    }

    public Page<PostDto> getAllPostsByUser(Boolean isSearchMode, final String email, final String keyword , final Integer pageNumber){
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return postsRepository.findAllByKeywordAndEmail(isSearchMode, keyword, email, pageable);

    }

    public Page<PostDto> getAllPosts(Boolean isSearchMode, final String keyword, final Integer pageNumber){
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return postsRepository.findAllByKeyword(isSearchMode, keyword, pageable );
    }

    public PostDto getPostById(Long postId) {
        return postsRepository.findById(postId, PostDto.class)
                .orElseThrow(() -> new NotFoundResourceException("Post not found with id: " + postId));
    }

    @Transactional
    public void updatePost(Long postId, PostDto postDto) {
        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new NotFoundResourceException("Post not found with id: " + postId));
        post.update(postDto);
    }

    @Transactional
    public void deletePost(Long postId) {
        postsRepository.deleteById(postId);
    }

}
