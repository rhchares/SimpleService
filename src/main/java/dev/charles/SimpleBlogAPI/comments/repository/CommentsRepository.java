package dev.charles.SimpleBlogAPI.comments.repository;

import dev.charles.SimpleBlogAPI.comments.domain.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsRepository extends JpaRepository<Comments,Long>, CustomizedCommentsRepository{
}
