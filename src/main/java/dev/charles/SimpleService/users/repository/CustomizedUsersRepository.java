package dev.charles.SimpleService.users.repository;

import dev.charles.SimpleService.users.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomizedUsersRepository {
    Page<UserDto> findAllByKeyword(Boolean isSearchMode, String keyword, Pageable pageable);

}
