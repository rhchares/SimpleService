package dev.charles.SimpleService.posts.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.charles.SimpleService.posts.dto.PostDto;
import dev.charles.SimpleService.users.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static dev.charles.SimpleService.posts.domain.QPosts.posts;
import static dev.charles.SimpleService.users.domain.QUsers.users;


@RequiredArgsConstructor
public class CustomizedPostsRepositoryImpl implements CustomizedPostsRepository{
    private final JPAQueryFactory queryFactory;


    @Override
    public List<PostDto> findAllByKeyword(String keyword, Pageable pageable) {
        return queryFactory
                .select(Projections.fields(PostDto.class,
                        posts.title,
                        posts.content))
                .from(posts)
                .where(
                        posts.title.likeIgnoreCase("%"+keyword + "%")
                )
                .orderBy(posts.id.desc())
                .limit(pageable.getPageSize())
                .offset((long) pageable.getPageSize() * pageable.getPageNumber())
                .fetch();
    }

    @Override
    public List<PostDto> findAllByKeywordAndEmail(String keyword, String email, Pageable pageable) {
        return queryFactory
                .select(Projections.fields(PostDto.class,
                        posts.title,
                        posts.content))
                .from(posts)
                .join(posts.createdBy, users)
                .where(
                        posts.title.likeIgnoreCase("%"+keyword + "%"),
                        users.email.eq(email)
                )
                .orderBy(posts.id.desc())
                .limit(pageable.getPageSize())
                .offset((long) pageable.getPageSize() * pageable.getPageNumber())
                .fetch();
    }

    @Override
    public Long countByKeyword(String keyword) {
        return queryFactory
                .select(posts.count())
                .from(posts)
                .where(
                        posts.title.likeIgnoreCase("%"+keyword + "%")
                )
                .fetchOne();
    }

    @Override
    public Long countByKeywordAndEmail(String keyword, String email) {
        return queryFactory
                .select(posts.count())
                .from(posts)
                .join(posts.createdBy, users)
                .where(
                        posts.title.likeIgnoreCase("%"+keyword + "%"),
                        users.email.eq(email)
                )
                .fetchOne();
    }
}
