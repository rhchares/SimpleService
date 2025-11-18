package dev.charles.SimpleService.comments.dto;

import dev.charles.SimpleService.comments.domain.Comments;
import dev.charles.SimpleService.users.dto.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@NoArgsConstructor
public class CommentsResponseDto {
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private UserDto createdBy; // 작성자 정보

    static public CommentsResponseDto of(Comments comment){
        return new CommentsResponseDto(comment);
    }

    public CommentsResponseDto(Comments comment) {
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.createdBy = UserDto.builder()
                .username(comment.getCreatedBy().getUsername())
                .email(comment.getCreatedBy().getEmail())
                .build();
    }
}