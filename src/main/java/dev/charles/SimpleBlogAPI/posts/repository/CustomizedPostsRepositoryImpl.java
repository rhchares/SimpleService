package dev.charles.SimpleBlogAPI.posts.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.charles.SimpleBlogAPI.posts.domain.Posts;
import dev.charles.SimpleBlogAPI.posts.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static dev.charles.SimpleBlogAPI.posts.domain.QPosts.posts;
import static dev.charles.SimpleBlogAPI.users.domain.QUsers.users;


public class CustomizedPostsRepositoryImpl extends QuerydslRepositorySupport implements CustomizedPostsRepository {
    private final JPAQueryFactory queryFactory;
    public CustomizedPostsRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Posts.class);
        this.queryFactory = queryFactory;
    }


    @Override
    public Page<PostDto> findAllByKeyword(boolean isSearchMode, String keyword, Pageable pageable) {

        JPAQuery<Long> idQuery = queryFactory
                .select(posts.id)
                .from(posts)
                .where(searchText(keyword))
                .orderBy(posts.id.desc());

        JPQLQuery<Long> paginationIdQuery = querydsl().applyPagination(pageable, idQuery);
        List<Long> ids = paginationIdQuery.fetch();
        if(ids.isEmpty()){
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(0,10), 0);
        }
        JPAQuery<PostDto> query = queryFactory.select(Projections.fields(PostDto.class,
                                        posts.title,
                                        posts.content))
                                .from(posts)
                                .where(
                                        posts.id.in(ids)
                                )
                                .orderBy(posts.id.desc());

        List<PostDto> content = query.fetch();
        if(isSearchMode) {
            int fixedPageCount = 10 * pageable.getPageSize();
            return new PageImpl<>(content, pageable, fixedPageCount);
        }
        Long totalCount = queryFactory
                .select(posts.id.count())
                .from(posts)
                .where(searchText(keyword))
                .fetchOne();
        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Page<PostDto> findAllByKeywordAndEmail(boolean isSearchMode, String keyword, String email, Pageable pageable) {
        JPAQuery<Long> idQuery = queryFactory
                .select(posts.id)
                .from(posts)
                .join(posts.createdBy, users)
                .where(
                        searchText(keyword),
                        posts.createdBy.email.eq(email)
                )
                .orderBy(posts.id.desc());
        JPQLQuery<Long> paginationId = querydsl().applyPagination(pageable, idQuery);
        List<Long> ids = paginationId.fetch();

        if(ids.isEmpty()){
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(0,10), 0);
        }
        JPAQuery<PostDto> query = queryFactory
                .select(Projections.fields(PostDto.class,
                        posts.title,
                        posts.content))
                .from(posts)
                .where(
                       posts.id.in(ids)
                )
                .orderBy(posts.id.desc());

        List<PostDto> content = query.fetch();

        if(isSearchMode) {
            int fixedPageCount = 10 * pageable.getPageSize();
            return new PageImpl<>(content, pageable, fixedPageCount);
        }

        Long totalCount = queryFactory
                .select(posts.id.count())
                .from(posts)
                .join(posts.createdBy, users)
                .where(
                        searchText(keyword),
                        posts.createdBy.email.eq(email)
                )
                .fetchOne();
        return new PageImpl<>(content, pageable, totalCount);


    }

    private Querydsl querydsl() {
        return Objects.requireNonNull(getQuerydsl());
    }

    private BooleanExpression searchText(String keyword) {
        String lang = "english";
        if(Optional.ofNullable(keyword).isEmpty()) return null;
        return Expressions.booleanTemplate(
                "search_text({0} ,{1}, {2})",
                posts.post_tsv,
                Expressions.constant(lang),
                Expressions.constant(keyword)
        );
    }

}
