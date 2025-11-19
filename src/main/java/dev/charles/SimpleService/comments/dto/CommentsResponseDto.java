package dev.charles.SimpleService.comments.dto;

import com.querydsl.core.annotations.QueryProjection;
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

    @QueryProjection
    public CommentsResponseDto(String content, Instant createdAt, Instant updatedAt, UserDto createdBy) {
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
    }
}