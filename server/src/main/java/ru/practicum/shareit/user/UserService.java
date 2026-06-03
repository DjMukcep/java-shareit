package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUser;
import ru.practicum.shareit.user.dto.UpdateUser;

public interface UserService {

    User createUser(NewUser request);

    User updateUser(Long userId, UpdateUser request);

    User getUser(long id);

    void deleteUser(long id);
}
