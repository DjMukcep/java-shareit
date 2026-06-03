package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.NewUser;
import ru.practicum.shareit.user.dto.UpdateUser;
import ru.practicum.shareit.user.dto.UserDto;

@UtilityClass
public class UserMapper {

    public static User toUser(NewUser newUserRequest) {
        User user = new User();
        user.setName(newUserRequest.getName());
        user.setEmail(newUserRequest.getEmail());
        return user;
    }

    public static User updateUserFields(User user, UpdateUser request) {
        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }

        if (request.hasUsername()) {
            user.setName(request.getName());
        }

        return user;
    }

    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }
}
