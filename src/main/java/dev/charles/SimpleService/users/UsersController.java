package dev.charles.SimpleService.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api/users", produces = "application/json")
@RequiredArgsConstructor
public class UsersController {
    final private UsersService usersService;

    /**
     * GET /api/users/{email}
     * 사용자의 정보 반환
     * @param email 조회할 사용자의 email
     * @return 사용자 데이터 (UserDto)
     */
    @GetMapping("/{email}")
    ResponseEntity<UserDto> getUser(@Validated @PathVariable("email") String email ){
        UserDto userDto =  usersService.getUserByEmail(email);
        return new ResponseEntity<>(userDto,HttpStatus.OK);
    }

    /**
     * GET /api/users/paged
     * 사용자의 목록을 페이징 처리하여 반환합니다.
     * @param offset 조회할 페이지 번호 (0부터 시작, 기본값 0)
     * @return 페이징된 사용자 데이터 (Page<UserDto>)
     */

    @GetMapping("/paged")
    ResponseEntity<Page<UserDto>> getUsers(@RequestParam Integer offset){
        var users = usersService.getUsers(offset);
        return new ResponseEntity<>(users,HttpStatus.OK);
    }

    /**
     * POST /api/users
     * 새로운 사용자 계정을 생성합니다.
     * @param userDto 생성할 사용자 정보 (username, email)
     * @return HTTP 201 CREATED 반환
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Validated @RequestBody UserDto userDto) {
        // Service 계층의 생성 메서드 호출
        usersService.create(userDto);
        // HTTP 201 Created와 함께 생성된 사용자 정보를 반환
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }


    /**
     * PUT /api/users/{id}
     * 특정 ID를 가진 사용자 정보를 수정합니다.
     * @param email 수정할 사용자의 email
     * @param userDto 수정할 사용자 정보 (username, email)
     * @return 수정된 사용자 정보 (UserDto)
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable("id") String email,
            @Validated @RequestBody UserDto userDto) {

        // Service 계층의 수정 메서드 호출 (id를 함께 전달)
        UserDto updatedUser = usersService.update(email, userDto);

        // HTTP 200 OK와 함께 수정된 사용자 정보를 반환
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * DELETE /api/users/{id}
     * 특정 ID를 가진 사용자 계정을 삭제합니다.
     * @param email 삭제할 사용자의 email
     * @return 응답 본문 없이 HTTP 204 No Content 반환
     */
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable("email") String email) {
        usersService.delete(email);
        return ResponseEntity.noContent().build();
    }



}
