package dev.charles.SimpleService.users;

import dev.charles.SimpleService.AbstractJpaRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UsersRepositoryTest extends AbstractJpaRepositoryTest {
    @Autowired
    private UsersRepository usersRepository;

    private Users user1;
    private Users user2;

    @BeforeEach
    void setUp() {
        usersRepository.deleteAll();

        user1 = Users.builder()
                .email("test1@email.com")
                .username("testUser").build();
        user2 =  Users.builder()
                .email("test2@email.com")
                .username("testUser2").build();

        usersRepository.save(user1);
        usersRepository.save(user2);
    }
    @Test
    @DisplayName("Get a user by email")
    void findByEmail_shouldReturnUsersEntity() {
        // When
        Optional<Users> foundUser = usersRepository.findByEmail("test1@email.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testUser");
    }

    @Test
    @DisplayName("Get a userDto by email")
    void findByEmail_withProjection_shouldReturnDto() {
        // When
        Optional<UserDto> foundDto = usersRepository.findByEmail("test2@email.com", UserDto.class);

        // Then
        assertThat(foundDto).isPresent();
        assertThat(foundDto.get().email()).isEqualTo("test2@email.com");
        // UserDto는 name 필드를 가질 것으로 가정
        assertThat(foundDto.get().username()).isEqualTo("testUser2");

        // 엔티티가 아닌 DTO 객체인지 확인 (null이면 엔티티인 경우가 있음)
        assertThat(foundDto.get()).isInstanceOf(UserDto.class);
    }

    @Test
    @DisplayName("Get pagination of users that is sorted by creation date descending (newest first)")
    void findAllByOrderByCreatedAtDesc_shouldReturnPagedData() {
        // Given
        Pageable pageable = PageRequest.of(0, 1); // 0페이지, 사이즈 1

        // When
        Page<UserDto> userPage = usersRepository.findAllByOrderByCreatedAtDesc(pageable);

        // Then
        assertThat(userPage.getTotalElements()).isEqualTo(2);
        assertThat(userPage.getContent()).hasSize(1);
        assertThat(userPage.getContent().get(0).username()).isEqualTo("testUser2");
        assertThat(userPage.isFirst()).isTrue();
    }

    @Test
    @DisplayName("Get page of users")
    void findAllByOrderByCreatedAtDesc_shouldReturnNextPage() {
        // Given
        Pageable pageable = PageRequest.of(1, 1); // 1페이지, 사이즈 1

        // When
        Page<UserDto> userPage = usersRepository.findAllByOrderByCreatedAtDesc(pageable);

        // Then
        assertThat(userPage.getTotalElements()).isEqualTo(2);
        assertThat(userPage.getContent()).hasSize(1);
        assertThat(userPage.getContent().get(0).username()).isEqualTo("testUser");
        assertThat(userPage.isLast()).isTrue();
    }

}
