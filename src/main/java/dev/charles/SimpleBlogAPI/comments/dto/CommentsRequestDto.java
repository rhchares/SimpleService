package dev.charles.SimpleBlogAPI.comments.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.PersistenceCreator;

@Getter
@NoArgsConstructor
@ToString
public class CommentsRequestDto {
    @NotNull(message = "Input Post id")
    private Long postId;
    private Long parentId;

    @Length(min = 5, message = "input more than 5 length")
    private String content;

    @Builder
    @PersistenceCreator
    public CommentsRequestDto(Long postId, Long parentId, String content){
        this.postId =postId;
        this.parentId = parentId;
        this.content=content;
    }
}