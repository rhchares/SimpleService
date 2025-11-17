package dev.charles.SimpleService.comments.dto;

import dev.charles.SimpleService.comments.domain.Comments;
import dev.charles.SimpleService.users.dto.UserDto;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class CommentsResponseDto {
    final private String content;
    final private Instant createdAt;
    final private UserDto createdBy; // 작성자 정보
    final private List<CommentsResponseDto> replies; // 대댓글 리스트 (재귀적 구조)

    static public CommentsResponseDto of(Comments comment){
        return new CommentsResponseDto(comment);
    }

    public CommentsResponseDto(Comments comment) {
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.createdBy = UserDto.builder()
                .username(comment.getCreatedBy().getUsername())
                .email(comment.getCreatedBy().getEmail())
                .build();
        this.replies = comment.getChildComments().stream()
                .sorted(Comparator.comparing(Comments::getCreatedAt).reversed())
                .map(CommentsResponseDto::new)
                .collect(Collectors.toList())
                ;
    }
}