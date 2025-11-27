package dev.charles.SimpleBlogAPI.posts;

import dev.charles.SimpleBlogAPI.posts.domain.Posts;
import dev.charles.SimpleBlogAPI.posts.dto.PostDto;
import dev.charles.SimpleBlogAPI.posts.repository.PostsRepository;
import dev.charles.SimpleBlogAPI.posts.service.PostsService;
import dev.charles.SimpleBlogAPI.users.domain.Users;
import dev.charles.SimpleBlogAPI.users.dto.UserDto;
import dev.charles.SimpleBlogAPI.users.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostsServiceTest {
    @Mock
    private PostsRepository postsRepository;
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private PostsService postsService;

    private Users user;
    private Posts post;
    private UserDto userDto;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .email("userdsd@email.com")
                .username("user").build();
        postDto = PostDto.builder()
                .title("post")
                .content("content").build();
        user = Users.of(userDto);
        post = Posts.of(postDto);
        post.setUser(user);
    }
    @Test
    void createPost() {
        //given
        given(usersRepository.findByEmail(any())).willReturn(Optional.of(user));
        String email = "email@gmail.com";
        //when
        postsService.createPost(email, postDto);

        //then
        verify(usersRepository, times(1)).findByEmail(any());
        verify(postsRepository, times(1)).save(any());
    }

    @Test
    void getAllPostsByKeyword() {
        //given
        String keyword = "hi";
        Pageable pageable = PageRequest.of(0,10);
        List<PostDto> dtoList = new ArrayList<>();
        Page<PostDto> givenResult= new PageImpl<>(dtoList, pageable, 5);
        for (int i = 0; i < 5; i++) {
            PostDto temp = PostDto.builder().title("hi"+i)
                            .content("content")
                                    .build();
            dtoList.add(temp);
        }
        given(postsRepository.findAllByKeyword( true, keyword,  pageable)).willReturn(givenResult);

        //when
        Page<PostDto> result = postsService.getAllPosts(true, keyword, 0);

        //then

        verify(postsRepository, times(1)).findAllByKeyword(true, keyword, pageable);

        assertThat(result).isEqualTo(givenResult);
    }

    @Test
    void getAllPostsByKeywordAndUser() {
        //given
        String keyword = "hi";
        Integer pageSize = 10;
        Pageable pageable = PageRequest.of(0,pageSize);
        Long total = 5L;
        String email = "sample@email.com";
        List<PostDto> dtoList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            PostDto temp = PostDto.builder().title("hi"+i)
                    .content("content")
                    .build();
            dtoList.add(temp);
        }

        Page<PostDto> givenResult = new PageImpl<>(dtoList, pageable, total);
        given(postsRepository.findAllByKeywordAndEmail(false, keyword, email, pageable) ).willReturn(givenResult);

        //when
        Page<PostDto> result = postsService.getAllPostsByUser(false, email, keyword, 0);

        //then
        verify(postsRepository, times(1)).findAllByKeywordAndEmail(false, keyword, email, pageable) ;

        result.forEach(
                t -> assertThat(t).isIn(dtoList)
        );
    }

    @Test
    void getPostById() {
        //given
        given(postsRepository.findById(any(), any())).willReturn(Optional.of(postDto));
        //when
        PostDto result = postsService.getPostById(1L);
        //then
        assertAll(
                ()-> assertThat(result.getTitle()).isEqualTo(post.getTitle()),
                ()-> assertThat(result.getContent()).isEqualTo(post.getContent())
        );
        verify(postsRepository, times(1)).findById(any(), any());
    }

    @Test
    void updatePost() {
        //given
        given(postsRepository.findById(any())).willReturn(Optional.of(post));
        PostDto newDto = PostDto.builder()
                .title("new title")
                .content("content sdsd")
                .build();
        //when
        postsService.updatePost(1L, newDto);

        //then
        assertAll(
                ()-> assertThat(post.getTitle()).isEqualTo(newDto.getTitle()),
                ()-> assertThat(post.getContent()).isEqualTo(newDto.getContent())
        );
        verify(postsRepository, times(1)).findById(any());
    }

    @Test
    void deletePost() {
        // when
        postsService.deletePost(1L);
        // then
        verify(postsRepository, times(1)).deleteById(any());
    }
}