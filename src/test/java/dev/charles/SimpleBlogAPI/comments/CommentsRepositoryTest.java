package dev.charles.SimpleBlogAPI.comments;

import dev.charles.SimpleBlogAPI.AbstractIntegrationTest;
import dev.charles.SimpleBlogAPI.comments.domain.Comments;
import dev.charles.SimpleBlogAPI.comments.dto.CommentsRequestDto;
import dev.charles.SimpleBlogAPI.comments.dto.CommentsResponseDto;
import dev.charles.SimpleBlogAPI.comments.repository.CommentsRepository;
import dev.charles.SimpleBlogAPI.posts.domain.Posts;
import dev.charles.SimpleBlogAPI.posts.dto.PostDto;
import dev.charles.SimpleBlogAPI.posts.repository.PostsRepository;
import dev.charles.SimpleBlogAPI.users.domain.Users;
import dev.charles.SimpleBlogAPI.users.dto.UserDto;
import dev.charles.SimpleBlogAPI.users.repository.UsersRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class CommentsRepositoryTest extends AbstractIntegrationTest {
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private UsersRepository usersRepository;

    @DisplayName("Given 1 user, 5 posts, and 5 parentComments which of one has 3 replies ")
    @Nested
    class ReadTest{
        private Posts curPost;
        private Users curUser;
        private Comments parentComment;
        private Pageable pageable;

        @BeforeEach
        void setup(){
            UserDto userDto = UserDto.builder()
                    .email("test@email.com")
                    .username("test").build();
            curUser = Users.of(userDto);
            usersRepository.save(curUser);
            for (int i = 5; i >= 1; i--) {
                PostDto temp = PostDto.builder().title("test"+i)
                        .content("content").build();
                Posts post = Posts.of(temp);
                curPost =postsRepository.save(post);
            }
            for (int i = 5; i >= 1; i--) {
                Comments comment = Comments.builder()
                        .post(curPost)
                        .user(curUser)
                        .content("comment"+i).build();
                parentComment =commentsRepository.save(comment);
            }
            // replies

            for (int i = 3; i >= 1; i--) {
                CommentsRequestDto temp = CommentsRequestDto.builder().content("replies"+i)
                        .parentId(curPost.getId()).build();
                Comments comment = Comments.builder()
                        .parentComment(parentComment)
                        .post(curPost)
                        .user(curUser)
                        .content("reply"+i).build();
                commentsRepository.save(comment);
            }

            pageable = PageRequest.of(0,10);
        }

        @AfterEach
        void tearDown(){
            commentsRepository.deleteAll();
            usersRepository.deleteAll();
            postsRepository.deleteAll();

        }

        @Nested
        @DisplayName("When we have postId")
        class PostId{
            private Long postId;
            @BeforeEach
            void setup(){
                postId = curPost.getId();
            }
            @Test
            @DisplayName("Then you can get only parent comments")
            void findAllParentsByPostId() {
                Page<CommentsResponseDto> result =  commentsRepository.findAllParentsByPostId(postId, pageable);
                assertSoftly((softly)-> {
                    softly.assertThat(result.getNumber()).isEqualTo(0);
                    softly.assertThat(result.getTotalElements()).isEqualTo(5);
                    softly.assertThat(result).extracting(CommentsResponseDto::getContent)
                                    .allMatch(content -> content.contains("comment"));
                });
            }

            @Test
            @DisplayName("Then you can get empty result when can't find any ids.")
            void findNothingByPostId() {
                Page<CommentsResponseDto> result =  commentsRepository.findAllParentsByPostId(98321L, pageable);
                assertSoftly((softly)-> {
                    softly.assertThat(result.getNumber()).isEqualTo(0);
                    softly.assertThat(result.getTotalElements()).isEqualTo(0);
                });
            }

        }

        @Nested
        @DisplayName("When we have parentId")
        class ParentId{
            private Long parentId;
            @BeforeEach
            void setup(){
                parentId = parentComment.getId();
            }
            @Test
            @DisplayName("Then you can have replies with parentId")
            void findAllParentsByPostId() {
                Page<CommentsResponseDto> result =  commentsRepository.findAllChildrenByParentId(parentId, pageable);
                assertSoftly((softly)-> {
                    softly.assertThat(result.getNumber()).isEqualTo(0);
                    softly.assertThat(result.getTotalElements()).isEqualTo(3);
                    softly.assertThat(result)
                            .extracting(CommentsResponseDto::getContent)
                            .allMatch(content -> content.contains("reply"));
                });
            }

            @Test
            @DisplayName("Then you can get empty result")
            void findNothingByPostId() {
                Page<CommentsResponseDto> result =  commentsRepository.findAllChildrenByParentId(98765431L, pageable);
                assertSoftly((softly)-> {
                    softly.assertThat(result.getNumber()).isEqualTo(0);
                    softly.assertThat(result.getTotalElements()).isEqualTo(0);
                });
            }
        }

    }

    @DisplayName("Given 1 user,1 post,and 1 parentComment ")
    @Nested
    class UpdateAndDeleteTest{
        private Posts curPost;
        private Users curUser;
        private Comments savedComment;
        private String newCotent;
        @BeforeEach
        void setup(){
            UserDto userDto = UserDto.builder()
                    .email("test@email.com")
                    .username("test").build();
            curUser = Users.of(userDto);
            usersRepository.save(curUser);
            PostDto temp = PostDto.builder().title("test")
                        .content("content").build();
            Posts post = Posts.of(temp);
            curPost =postsRepository.save(post);

            Comments comment = Comments.builder()
                    .post(curPost)
                    .user(curUser)
                    .content("comment").build();
            savedComment = commentsRepository.save(comment);
            newCotent = "new comment";
        }

        @AfterEach
        void tearDown(){
            commentsRepository.deleteAll();
            usersRepository.deleteAll();
            postsRepository.deleteAll();

        }
        @Nested
        @DisplayName("When we have a post, user, and content")
        class newComment{
            @Test
            @DisplayName("Then you can delete a existed comment")
            void delete(){
                commentsRepository.delete(savedComment);
                System.out.println(commentsRepository.findAll());
                assertThat(commentsRepository.count()).isEqualTo(0);
            }

            @Test
            @DisplayName("Then you can create a comment with post, user, and content")
            void create(){
                Comments newComment = Comments.builder()
                        .post(curPost)
                        .user(curUser)
                        .content(newCotent)
                        .build();
                Comments saved = commentsRepository.save(newComment);

                assertThat(commentsRepository.count()).isEqualTo(2);

            }


        }

    }



}
