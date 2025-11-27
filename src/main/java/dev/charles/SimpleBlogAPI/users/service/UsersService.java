package dev.charles.SimpleBlogAPI.users.service;

import dev.charles.SimpleBlogAPI.errors.exception.DuplicateResourceException;
import dev.charles.SimpleBlogAPI.errors.exception.NotFoundResourceException;
import dev.charles.SimpleBlogAPI.users.dto.UserDto;
import dev.charles.SimpleBlogAPI.users.domain.Users;
import dev.charles.SimpleBlogAPI.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsersService {
    final private UsersRepository usersRepository;

    public UserDto getUserByEmail (String email){
        return usersRepository.findByEmail(email, UserDto.class).orElseThrow(
                () -> new NotFoundResourceException("Not found user by email")
        );
    }

    public Page<UserDto> getUsers(Boolean isSearchMode, final String keyword, final Integer pageNumber){
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return usersRepository.findAllByKeyword(isSearchMode, keyword, pageable);

    }

    @Transactional
    @PreAuthorize("principal.claims['email'] == #userDto.email")
    public void create(final UserDto userDto){
        isDuplicated(userDto.getEmail());
        Users user = Users.of(userDto);
        usersRepository.save(user);
    }

    @Transactional
    public void delete(final String email){
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundResourceException("Not found user by email"));
        usersRepository.delete(user);
    }

    @Transactional
    public void update(final String email, final UserDto userDto){
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundResourceException("Not found user by email"));
        isDuplicated(userDto.getEmail());
        user.update(userDto);
    }

    private void isDuplicated(String email){
        if(usersRepository.findByEmail(email).isPresent()){
            throw new DuplicateResourceException("Already user existed by email");
        }
    }

}

