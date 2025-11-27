package dev.charles.SimpleBlogAPI.comments.service;

import dev.charles.SimpleBlogAPI.comments.domain.Comments;
import dev.charles.SimpleBlogAPI.comments.dto.CommentsRequestDto;
import dev.charles.SimpleBlogAPI.comments.dto.CommentsResponseDto;
import dev.charles.SimpleBlogAPI.comments.repository.CommentsRepository;
import dev.charles.SimpleBlogAPI.errors.exception.NotAuthorizedException;
import dev.charles.SimpleBlogAPI.errors.exception.NotFoundResourceException;
import dev.charles.SimpleBlogAPI.posts.domain.Posts;
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
public class CommentsService {
    private final CommentsRepository commentsRepository;
    private final PostsRepository postsRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public void createComment(final CommentsRequestDto requestDto, final String email) {
        Posts post =postsRepository.findById(requestDto.getPostId())
                .orElseThrow(()-> new NotFoundResourceException("Post not found by id: "+ requestDto.getPostId()));
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new NotFoundResourceException("User not found by email: " + email));
        Comments parentComment = commentsRepository.findById(requestDto.getParentId()).orElseGet(()-> null);
        Comments newComment = Comments.builder()
                .content(requestDto.getContent())
                .user(user)
                .post(post)
                .parentComment(parentComment)
                .build();
        commentsRepository.save(newComment);
    }

    public Page<CommentsResponseDto> getCommentsByPostId(final Long postId, final Integer pageNumber) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return commentsRepository.findAllParentsByPostId(postId, pageable);

    }

    public Page<CommentsResponseDto> getRepliesByParentId(Long parentId, final Integer pageNumber) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return commentsRepository.findAllChildrenByParentId(parentId, pageable);

    }

    @Transactional
    public void updateComment(final Long commentId, final String updateComment, final String email) {
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundResourceException("Comment not found by id: "+commentId));
        hasAuthorized(comment.getCreatedBy(), email);
        comment.update(updateComment);
    }

    @Transactional
    public void deleteComment(final Long commentId, final String email) {
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundResourceException("Comment not found by id: "+commentId));
        hasAuthorized(comment.getCreatedBy(), email);
        commentsRepository.delete(comment);
    }

    private void hasAuthorized(final Users user, final String email){
        if(!user.getEmail().equals(email)) {
            throw new NotAuthorizedException("You're not writer on this comment.");
        }
    }

}
