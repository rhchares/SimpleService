package dev.charles.SimpleService.posts;

import dev.charles.SimpleService.AbstractIntegrationTest;
import dev.charles.SimpleService.posts.domain.Posts;
import dev.charles.SimpleService.posts.dto.PostDto;
import dev.charles.SimpleService.posts.repository.PostsRepository;
import dev.charles.SimpleService.users.domain.Users;
import dev.charles.SimpleService.users.dto.UserDto;
import dev.charles.SimpleService.users.repository.UsersRepository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class PostsRepositoryTest extends AbstractIntegrationTest {
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private UsersRepository usersRepository;


    @Nested
    @DisplayName("Given we have 5 posts and 1 user and 1 postDto")
    class CommonPostsRepositoryTest{
        private PostDto postDto = PostDto.builder()
                .title("post0")
                .content("content0").build();
        @BeforeEach
        void setup(){
            UserDto userDto1 = UserDto.builder()
                    .username("test1")
                    .email("hi@email.com").build();
            UserDto userDto2 = UserDto.builder()
                    .username("test2")
                    .email("test2@email.com").build();
            Users user1 = Users.of(userDto1);
            Users user2 = Users.of(userDto2);
            usersRepository.save(user1);
            usersRepository.save(user2);
            for (int i = 0; i < 5; i++) {
                PostDto tempDto = PostDto.builder().content("content"+i)
                        .title("post"+i).build();
                Posts post = Posts.of(tempDto);
                post.setUser(i%2== 0 ? user1 : user2);
                postsRepository.save(post);
            }
        }
        @AfterEach
        void teardown(){
            postsRepository.deleteAll();
            usersRepository.deleteAll();
        }

        @Nested
        @DisplayName("When access postsRepository with Id")
        class accessWithId{
            private Long id;
            @BeforeEach
            void setup(){
                id = postsRepository.findAll().getFirst().getId();
            }

            @Test
            @DisplayName("Then you can receive post by Id")
            void getPostTest(){
                //when
                Posts newPostDto = postsRepository.findById(id)
                        .orElseThrow();
                //then
                assertThat(newPostDto)
                        .extracting("title", "content")
                        .contains(postDto.getContent(),postDto.getTitle());
            }
            @Test
            @DisplayName("Then you can receive postDto by Id")
            void getPostDtoTest(){
                //when
                Optional<PostDto> newPostDto = postsRepository.findById(id, PostDto.class);
                //then
                assertThat(newPostDto.get())
                        .extracting("title", "content")
                        .contains(postDto.getContent(),postDto.getTitle());
            }
            @Test
            @DisplayName("Then you can delete post by Id")
            void deletePostbyIdTest(){
                //when
                postsRepository.deleteById(id);

                //then
                assertThat(postsRepository.count()).isEqualTo(4L);
            }

        }
        @Nested
        @DisplayName("When access postsRepository with PostDto")
        class accessWithPostDto{
            @BeforeEach
            void setup(){
                postsRepository.deleteAll();
            }
            @Test
            @DisplayName("Then you can save post")
            void savePost() {
                // when
                postsRepository.save(Posts.of(postDto));
                // then
                assertThat(postsRepository.count()).isEqualTo(1L);
            }

        }

    }

    @Nested
    @DisplayName("Given a isSearchMode , a user, a keyword , and a pageable, a email")
    class PaginationWithNotUsingSearchButtonTest{
        private Boolean isSearchMode;
        private Integer pageSize;
        private Pageable pageable;


        @BeforeEach
        void setup(){
            pageSize = 10;
            String email = "sample@email.com";
            String email2 = "sample2@email.com";
            List<Posts> posts = new ArrayList<>();
            List<String> postTitle = List.of(
                    "The Rise of Serverless Computing: When to Ditch the Dedicated Server",
                    "A Deep Dive into Rust's Ownership Model for C++ Developers",
                    "Mastering Concurrency: Understanding ThreadLocalRandom in Java",
                    "Beyond the Horizon: The Search for Exoplanets and Extraterrestrial Life",
                    "Decoding the Double Helix: Recent Breakthroughs in CRISPR Gene Editing"
            );
            UserDto userDto1 = UserDto.builder()
                    .username("test1")
                    .email(email).build();
            UserDto userDto2 = UserDto.builder()
                    .username("test2")
                    .email(email2).build();
            Users user1 = Users.of(userDto1);
            Users user2 = Users.of(userDto2);
            usersRepository.save(user1);
            usersRepository.save(user2);

            for (int i = 0; i < 50; i++) {
                PostDto dto =  PostDto.builder()
                        .title(postTitle.get(i%5))
                        .content("none").build();
                Posts temp = Posts.of(dto);
                temp.setUser(i%2 == 0 ? user1 : user2);
                posts.add(temp);
            }
            postsRepository.saveAll(posts);
        }

        @AfterEach
        void tearDown(){
            postsRepository.deleteAll();
            usersRepository.deleteAll();
        }

        @Nested
        @DisplayName("When we access a pagination method with parameters where isSearchMode is true")
        class SearchMode{
            @BeforeEach
            void setup(){
                isSearchMode= true;
            }

            @ParameterizedTest
            @CsvSource({"the, 0, 100","a, 0, 100", "asdasdgiojwrgiow, 0, 0"})
            @DisplayName("Then you can receive pagination of postDto by keyword")
            void findAllByKeywordTest(String keyword, int pageNumber, int expectedTotal) {
                System.out.println("total: " +postsRepository.findAll().size());
                pageable = PageRequest.of(pageNumber, pageSize);
                Page<PostDto> page = postsRepository.findAllByKeyword(isSearchMode, keyword,  pageable );
                assertThat(page.getNumber()).isEqualTo(pageNumber);
                assertThat(page.getTotalElements()).isEqualTo(expectedTotal);
            }

            @ParameterizedTest
            @CsvSource({", vfdfdf, 0, 0", ", sample@email.com, 1, 100", ", sample2@email.com, 0, 100"})
            @DisplayName("Then you can receive a pagination of postDto  by keyword and email")
            void findAllByKeywordByEmailTest(String keyword, String email, Integer pageNumber, Long expedtedTotal) {
                pageable = PageRequest.of(pageNumber, pageSize);
                Page<PostDto> page = postsRepository.findAllByKeywordAndEmail(isSearchMode, keyword, email, pageable);
                // then
                assertThat(page.getNumber()).isEqualTo(pageNumber);
                assertThat(page.getTotalElements()).isEqualTo(expedtedTotal);
            }
        }

        @Nested
        @DisplayName("When we access a pagination method with parameters where isSearchMode is false")
        class NotSearchMode{
            @BeforeEach
            void setup(){
                isSearchMode= false;
            }
            @ParameterizedTest
            @CsvSource({", 0, 50", "the, 0, 30", "asdasdgiojwrgiow, 0, 0"})
            @DisplayName("Then you can receive pagination of postDto by keyword")
            void findAllByKeywordTest(String keyword, int pageNumber, int expectedTotal) {
                pageable = PageRequest.of(pageNumber, pageSize);
                Page<PostDto> page = postsRepository.findAllByKeyword(isSearchMode, keyword,  pageable );
                assertThat(page.getNumber()).isEqualTo(pageNumber);
                assertThat(page.getTotalElements()).isEqualTo(expectedTotal);
            }
            @ParameterizedTest
            @CsvSource({", dsd, 0, 0", "the, sample@email.com, 0, 15", "the, sample2@email.com, 0, 15"})
            @DisplayName("Then you can receive a pagination of postDto  by keyword and email")
            void findAllByKeywordByEmailTest(String keyword, String email, int pageNumber, int expectedTotal){
                pageable = PageRequest.of(pageNumber, pageSize);
                Page<PostDto> page = postsRepository.findAllByKeywordAndEmail(isSearchMode, keyword, email, pageable);
                assertSoftly(softly->{
                    softly.assertThat(page.getNumber()).isEqualTo(pageNumber);
                    softly.assertThat(page.getTotalElements()).isEqualTo(expectedTotal);
                });
            }
        }

    }


}
