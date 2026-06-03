package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.NewUser;
import ru.practicum.shareit.user.dto.UpdateUser;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional(readOnly = true)
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    @DisplayName("Создание пользователя: успешное сохранение в базу")
    void createUser() {
        NewUser newUser = new NewUser();
        newUser.setName("user name");
        newUser.setEmail("some@email.com");

        User user = userService.createUser(newUser);

        assertNotNull(user.getId());
        assertEquals("user name", user.getName());
        assertEquals("some@email.com", user.getEmail());

        User foundUser = userRepository.findByEmail("some@email.com").orElseThrow();
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getEmail(), foundUser.getEmail());
        assertEquals(user.getName(), foundUser.getName());
    }

    @Test
    @Transactional
    @DisplayName("Обновление пользователя: успешное изменение данных")
    void updateUser() {
        User user = new User();
        user.setName("old name");
        user.setEmail("old@email.com");
        user = userRepository.save(user);

        UpdateUser request = new UpdateUser();
        request.setName("new name");
        request.setEmail("new@email.com");

        User updated = userService.updateUser(user.getId(), request);

        assertEquals(user.getId(), updated.getId());
        assertEquals("new name", updated.getName());
        assertEquals("new@email.com", updated.getEmail());

        User fromDB = userRepository.findById(user.getId()).orElseThrow();

        assertEquals("new name", fromDB.getName());
        assertEquals("new@email.com", fromDB.getEmail());

    }

    @Test
    @DisplayName("Найти пользователя: успешное получение данных")
    void getUser() {
        User user = new User();
        user.setName("name");
        user.setEmail("some@email.com");
        user = userRepository.save(user);

        User foundUser = userService.getUser(user.getId());

        assertEquals(user.getId(), foundUser.getId());
        assertEquals("some@email.com", foundUser.getEmail());
        assertEquals("name", foundUser.getName());
    }

    @Test
    @Transactional
    @DisplayName("Удаление пользователя: успешное удаление данных")
    void deleteUser() {
        User user = new User();
        user.setName("name");
        user.setEmail("some@email.com");
        user = userRepository.save(user);

        Long userId = user.getId();

        userService.deleteUser(userId);

        assertFalse(userRepository.existsById(userId));
    }
}