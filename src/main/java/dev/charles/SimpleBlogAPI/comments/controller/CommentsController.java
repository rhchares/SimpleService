package dev.charles.SimpleBlogAPI.comments.controller;

import dev.charles.SimpleBlogAPI.comments.dto.CommentsRequestDto;
import dev.charles.SimpleBlogAPI.comments.dto.CommentsResponseDto;
import dev.charles.SimpleBlogAPI.comments.service.CommentsService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/comments", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class CommentsController {
    final private CommentsService commentsService;

    @PostMapping
    public ResponseEntity<?> createComment(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @RequestBody CommentsRequestDto requestDto) {
        String email = principal.getAttribute("email");
        commentsService.createComment(requestDto, email);
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity<?> updateComment(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @RequestParam(value = "commentId") Long commentId,
            @RequestParam(value = "updateComment") String updateComment) {
        String email = principal.getAttribute("email");
        commentsService.updateComment(commentId,updateComment,email);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteComment(
            @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
            @RequestParam(value = "commentId") Long commentId) {
        String email = principal.getAttribute("email");
        commentsService.deleteComment(commentId,email);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/paged/post")
    public ResponseEntity<?> getPostComments(
            @NotNull @RequestParam(value = "postId") Long postId,
            @RequestParam(value = "pageNumber") Integer pageNumber) {
        Page<CommentsResponseDto> result =commentsService.getCommentsByPostId(postId,pageNumber);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/paged/reply")
    public ResponseEntity<?> getReplies(
            @NotNull @RequestParam(value = "parentId") Long parentId,
            @RequestParam(value = "pageNumber") Integer pageNumber) {
        Page<CommentsResponseDto> result = commentsService.getRepliesByParentId(parentId,pageNumber);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
