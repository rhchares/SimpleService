package dev.charles.SimpleBlogAPI.comments.dto;

import com.querydsl.core.annotations.QueryProjection;
import dev.charles.SimpleBlogAPI.users.dto.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

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