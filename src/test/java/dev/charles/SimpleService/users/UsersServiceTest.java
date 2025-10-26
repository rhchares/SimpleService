package dev.charles.SimpleService.users;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest{
    @InjectMocks
    private UsersService usersService;
    @Mock
    private UsersRepository usersRepository;

    @Nested
    @DisplayName("Given there are two registered users")
    public class RegisteredTwoUsersTest{
        //GIVEN
        private Optional<Users> mike;
        private UserDto mikeDto;
        private Optional<Users> john;
        private UserDto johnDto;

        @BeforeEach
        void setup(){
            mike = Optional.of(Users.builder()
                    .username("mike")
                    .email("mike@gmail.com").build());
            mikeDto = new UserDto("mike","mike@gmail.com");
            john = Optional.of(Users.builder()
                    .username("john")
                    .email("john@gmail.com").build());
            johnDto = new UserDto("john","john@gmail.com");
        }

        @Nested
        @DisplayName("When use service")
        class GetMikeEntityByEmailTest{

            @Test
            @DisplayName("Then  find mike of a user entity by email and repository is called ")
            public void UserGetTest() {
                //given
                String targetEmail = "mike@gmail.com";
                given(usersRepository.findByEmail("mike@gmail.com", UserDto.class))
                        .willReturn(Optional.of(mikeDto));

                //when
                UserDto result = usersService.getUserByEmail(targetEmail);
                // then
                verify(usersRepository).findByEmail(targetEmail, UserDto.class);
                assertThat(result).extracting("email", "username")
                        .contains("mike","mike@gmail.com");
            }
            @Test
            @DisplayName("Then the repository is called with correct Pageable and returns the Page")

            void getUsers_ShouldCallRepositoryWithCorrectPageable() {
                Page<UserDto> mockPage;
                int targetOffset = 2; // 3번째 페이지 (인덱스 2)
                final int pageSize = 10;
                // When: 3번째 페이지 (offset=2) 요청
                List<UserDto> pageContent = List.of(
                        new UserDto("user21", "u21@mail.com"),
                        new UserDto("user22", "u22@mail.com")
                );
                // PageImpl(컨텐츠, Pageable, 총 요소 수)
                mockPage = new PageImpl<>(pageContent, PageRequest.of(targetOffset, pageSize), 50);

                // Given: Repository가 targetOffset으로 호출되면 mockPage를 반환하도록 설정
                given(usersRepository.findAllBy(
                        PageRequest.of(targetOffset, pageSize),
                        UserDto.class
                )).willReturn(mockPage);

                Page<UserDto> resultPage = usersService.getUsers(targetOffset);

                // Then 1: Repository가 올바른 Pageable 객체로 호출되었는지 검증
                verify(usersRepository).findAllBy(
                        // Pageable 객체의 내부 상태(PageNumber, PageSize) 검증
                        assertArg(p -> {
                            assertThat(p.getPageNumber()).as("Page Number").isEqualTo(targetOffset);
                            assertThat(p.getPageSize()).as("Page Size").isEqualTo(pageSize);
                        }),
                        // DTO Class 타입 검증
                        assertArg(type -> assertThat(type).isEqualTo(UserDto.class))
                );

                // Then 2: 서비스가 Repository의 반환 값을 그대로 전달했는지 검증
                assertThat(resultPage).as("Returned Page").isSameAs(mockPage);
                assertThat(resultPage.getTotalElements()).as("Total Elements").isEqualTo(50);
                assertThat(resultPage.getNumberOfElements()).as("Number of Elements").isEqualTo(2);
            }
            @Test
            @DisplayName("Then the repository's save method is called with a Users entity")
            void UserCreateTest() {
                // when
                usersService.create(mikeDto);
                // then
                verify(usersRepository).save(any());
            }

            @Test
            @DisplayName("Then find the user entity by email and then the entity is deleted")
            void UserDeleteTest() {
                given(usersRepository.findByEmail("mike@gmail.com"))
                        .willReturn(mike);
                // when
                usersService.delete("mike@gmail.com");
                // then
                verify(usersRepository).delete(any());
            }

            @Test
            @DisplayName("Then find the user by email and update the user properties by given parameters")
            void UserUpdate(){
                given(usersRepository.findByEmail("mike@gmail.com")).willReturn(mike);
                UserDto newDto = new UserDto("mike2", "mike@gmail.com");

                UserDto result = usersService.update("mike@gmail.com", newDto);

                verify(usersRepository).findByEmail("mike@gmail.com");
                verify(usersRepository).save(mike.get());
                assertThat(result).extracting("username","email")
                        .contains("mike2", "mike@gmail.com");
            }



        }

    }


}