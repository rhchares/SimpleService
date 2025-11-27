package dev.charles.SimpleBlogAPI.users.repository;

import dev.charles.SimpleBlogAPI.users.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomizedUsersRepository {
    Page<UserDto> findAllByKeyword(Boolean isSearchMode, String keyword, Pageable pageable);

}
