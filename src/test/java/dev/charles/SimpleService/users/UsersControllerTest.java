package dev.charles.SimpleService.users;

import dev.charles.SimpleService.errors.errorcode.CommonErrorCode;
import dev.charles.SimpleService.errors.errorcode.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsersController.class)
@DisplayName("UsersController 테스트")
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTP 요청 시뮬레이션을 위한 객체


    private ObjectMapper objectMapper = new ObjectMapper(); // JSON 직렬화를 위한 객체

    @MockitoBean
    private UsersService usersService; // Controller의 의존성 Mocking

    private final UserDto testUserDto = new UserDto("tester", "tesdt@example.com");
    private final String targetEmail = "tesdt@example.com";
    private final String targetId = "tesdt@example.com"; // updateUser의 path variable

    @BeforeEach
    void setup() {

    }

    @Test
    @DisplayName("GET /api/users/{email} 요청 시, 200 OK와 사용자 DTO를 반환해야 한다.")
    void getUser_ShouldReturnUserDto() throws Exception {
        // Given
        given(usersService.getUserByEmail(targetEmail)).willReturn(testUserDto);

        // When & Then
        mockMvc.perform(get("/api/users/{email}", targetEmail)
                        .accept(MediaType.APPLICATION_JSON)) // JSON 응답을 기대
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("tester"))
                .andExpect(jsonPath("$.email").value(targetEmail));

        // Service 호출 검증
        then(usersService).should().getUserByEmail(targetEmail);
    }

    @Test
    @DisplayName("GET /api/users/paged 요청 시, 200 OK와 페이징된 사용자 목록을 반환해야 한다.")
    void getUsers_ShouldReturnPagedUsers() throws Exception {
        // Given
        final int offset = 1;
        final int pageSize = 10;
        final PageRequest pageable = PageRequest.of(offset, pageSize);

        List<UserDto> userList = List.of(testUserDto);
        Page<UserDto> mockPage = new PageImpl<>(userList, pageable, 20);

        given(usersService.getUsers(offset)).willReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/users/paged")
                        .param("offset", String.valueOf(offset)) // 쿼리 파라미터 전달
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].email").value(targetEmail)) // 데이터 검증
                .andExpect(jsonPath("$.totalElements").value(20)); // 페이징 메타데이터 검증

        // Service 호출 검증 (offset=1로 호출되었는지)
        then(usersService).should().getUsers(offset);
    }

    // ------------------- C R E A T E -------------------

    @Test
    @DisplayName("POST /api/users 요청 시, 201 CREATED 상태를 반환하고 Service를 호출해야 한다.")
    void createUser_ShouldReturn201Created() throws Exception {
        // Given
        // usersService.create()는 void (혹은 DTO 반환)이며, 여기서는 서비스 호출만 검증
        // Controller 코드가 new ResponseEntity<>(null, HttpStatus.CREATED)를 반환하므로 DTO 반환은 테스트하지 않음.

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(testUserDto))
                ) // DTO를 JSON 본문으로 전송
                .andExpect(status().isCreated()) // 201 CREATED 상태 확인
                .andExpect(content().string("")); // 반환 본문이 비어 있는지 확인 (null이 String으로 직렬화됨)

        // Service 호출 검증
        then(usersService).should().create(testUserDto);
    }

    @Test
    @DisplayName("Response with BAD_REQUEST When invalid UserDto")
    void CreateUserThrowValidationException() throws Exception {
        // Given
        // usersService.create()는 void (혹은 DTO 반환)이며, 여기서는 서비스 호출만 검증
        // Controller 코드가 new ResponseEntity<>(null, HttpStatus.CREATED)를 반환하므로 DTO 반환은 테스트하지 않음.
        UserDto invalid = new UserDto("d", "d");

        ErrorCode error = CommonErrorCode.INVALID_PARAMETER;
        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(invalid))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(error.name()))
                .andExpect(jsonPath("$.message").value(error.getMessage()))
                .andExpect(jsonPath("$.errors[?(@.message == '올바른 이름을 입력하세요.')]").exists())
                .andExpect(jsonPath("$.errors[?(@.message == '5이상 15이하 글자를 입력하세요.')]").exists());


        // Service 호출 검증
        then(usersService).shouldHaveNoInteractions();
    }

    // ------------------- U P D A T E -------------------

    @Test
    @DisplayName("PUT /api/users/{id} 요청 시, 200 OK와 수정된 DTO를 반환해야 한다.")
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        // Given
        UserDto updatedDto = new UserDto("updatedName", targetEmail);
        // Controller의 updateUser는 PathVariable의 "id"를 String email로 받습니다.
        given(usersService.update(targetId, updatedDto)).willReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/users/{id}", targetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedName"));

        // Service 호출 검증 (targetId=email이 인자로 전달되었는지)
        then(usersService).should().update(targetId, updatedDto);
    }

    @Test
    @DisplayName("PUT /api/users/{id} 요청 시, Validation 오류로 400 Status 발생")
    void UpdateUserValidationException() throws Exception {
        // Given
        UserDto updatedDto = new UserDto("up", targetEmail);
        // Controller의 updateUser는 PathVariable의 "id"를 String email로 받습니다.
        given(usersService.update(targetId, updatedDto)).willReturn(updatedDto);
        ErrorCode INVALID_PARAMETER = CommonErrorCode.INVALID_PARAMETER;
        // When & Then
        mockMvc.perform(put("/api/users/{id}", targetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_PARAMETER.name()))
                .andExpect(jsonPath("$.message").value(INVALID_PARAMETER.getMessage()))
                .andExpect(jsonPath("$.errors[?(@.message == '올바른 이름을 입력하세요.')]").exists())
                .andExpect(jsonPath("$.errors[?(@.message == '5이상 15이하 글자를 입력하세요.')]").exists());

        // Service 호출 검증 (targetId=email이 인자로 전달되었는지)

        then(usersService).shouldHaveNoInteractions();
    }

    // ------------------- D E L E T E -------------------

    @Test
    @DisplayName("DELETE /api/users/{email} 요청 시, 204 NO CONTENT를 반환하고 Service를 호출해야 한다.")
    void deleteUser_ShouldReturn204NoContent() throws Exception {
        // Given: usersService.delete()는 void 반환이므로 Mocking 불필요

        // When & Then
        mockMvc.perform(delete("/api/users/{email}", targetEmail))
                .andExpect(status().isNoContent()) // 204 NO CONTENT 상태 확인
                .andExpect(content().string("")); // 응답 본문이 비어 있는지 확인

        // Service 호출 검증
        then(usersService).should().delete(targetEmail);
    }
}