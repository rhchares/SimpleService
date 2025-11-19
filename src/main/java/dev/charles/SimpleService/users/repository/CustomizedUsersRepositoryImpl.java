package dev.charles.SimpleService.users.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.charles.SimpleService.users.dto.QUserDto;
import dev.charles.SimpleService.users.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static dev.charles.SimpleService.users.domain.QUsers.users;


@RequiredArgsConstructor
public class CustomizedUsersRepositoryImpl implements CustomizedUsersRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserDto> findAllByKeyword(String keyword, Pageable pageable) {
                return queryFactory
                .select(new QUserDto(users.email, users.username))
                .from(users)
                .where(
                        users.username.likeIgnoreCase("%"+keyword + "%")
                )
                .orderBy(users.id.desc())
                .limit(pageable.getPageSize())
                .offset((long) pageable.getPageSize() * pageable.getPageNumber())
                .fetch();
    }

    @Override
    public Long countByKeyword(String keyword) {
        return queryFactory
                .select(users.count())
                .from(users)
                .where(
                        users.username.likeIgnoreCase("%"+keyword + "%")
                )
                .fetchOne();
    }

}
