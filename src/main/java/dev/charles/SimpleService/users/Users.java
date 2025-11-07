package dev.charles.SimpleService.users;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Users extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Builder
    public Users(String username, String email){
        this.username = username;
        this.email = email;
    }

    public void update(UserDto userDto){
        this.username = userDto.getUsername();
        this.email = userDto.getEmail();
    }

}
