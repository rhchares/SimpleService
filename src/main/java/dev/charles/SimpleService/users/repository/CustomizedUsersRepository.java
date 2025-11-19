package dev.charles.SimpleService.users.repository;

import dev.charles.SimpleService.users.dto.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomizedUsersRepository {
    List<UserDto> findAllByKeyword(String keyword, Pageable pageable);
    Long countByKeyword(String keyword);

}
