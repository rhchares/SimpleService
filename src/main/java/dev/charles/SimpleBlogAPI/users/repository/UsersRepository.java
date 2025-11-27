package dev.charles.SimpleBlogAPI.users.repository;

import dev.charles.SimpleBlogAPI.users.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long>, CustomizedUsersRepository {
    Optional<Users> findByEmail(String email);
    <T> Optional<T> findByEmail(String email, Class<T> type);

}
