package com.mailmak.time_registration_system.mappers;

import com.mailmak.time_registration_system.classes.User;
import com.mailmak.time_registration_system.dto.users.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserResponseMapper implements ModelMapper<User, UserResponse> {

    public UserResponseMapper() {}

    @Override
    public UserResponse mapTo(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }
}
