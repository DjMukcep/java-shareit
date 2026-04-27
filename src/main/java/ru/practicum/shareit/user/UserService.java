package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public User createUser(NewUserRequest request) {
        if (userRepository.isEmailExists(request.getEmail())) {
            throw new DuplicatedDataException("Электроный адрес: " + request.getEmail() + " уже существует.");
        }

        User user = UserMapper.mapToUser(request);
        user = userRepository.createUser(user);
        log.info("Новый пользователь: {}", user);

        return user;
    }

    public User updateUser(Long userId, UpdateUserRequest request) {
        if (request.hasEmail() && userRepository.isEmailExists(request.getEmail())) {
            throw new DuplicatedDataException("Электроный адрес: " + request.getEmail() + " уже существует.");
        }

        User updatedUser = userRepository.getUserById(userId)
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));

        updatedUser = userRepository.updateUser(updatedUser);
        log.info("Пользователь обновлен: {}", updatedUser);

        return updatedUser;
    }

    public User getUser(long id) {
        return userRepository.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден."));
    }

    public void deleteUser(long id) {
        User user = getUser(id);
        userRepository.deleteUserById(user.getId());
    }

}
