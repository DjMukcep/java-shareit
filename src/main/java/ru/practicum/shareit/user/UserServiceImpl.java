package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUser;
import ru.practicum.shareit.user.dto.UpdateUser;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    @Transactional
    public User createUser(NewUser request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicatedDataException("Пользователь с почтой: " + request.getEmail() + " уже существует.");
        }

        User user = UserMapper.toUser(request);
        user = userRepository.save(user);
        log.info("Новый пользователь: {}", user);

        return user;
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UpdateUser request) {
        if (request.hasEmail() && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicatedDataException("Пользователь с почтой: " + request.getEmail() + " уже существует.");
        }

        User updatedUser = userRepository.findById(userId)
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));

        updatedUser = userRepository.save(updatedUser);
        log.info("Пользователь обновлен: {}", updatedUser);

        return updatedUser;
    }

    @Override
    public User getUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден."));
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        User user = getUser(id);
        userRepository.deleteById(user.getId());
        log.info("Пользователь удален id: {}", user.getId());
    }
}
