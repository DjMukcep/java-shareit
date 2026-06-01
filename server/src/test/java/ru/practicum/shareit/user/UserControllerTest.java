package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUser;
import ru.practicum.shareit.user.dto.UpdateUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Добавить нового пользователя.")
    @SneakyThrows
    void createUser() {
        NewUser newUser = new NewUser();
        newUser.setEmail("email");
        newUser.setName("name");

        User savedUser = new User();
        savedUser.setEmail("email");
        savedUser.setName("name");
        savedUser.setId(1L);

        when(userService.createUser(any(NewUser.class))).thenReturn(savedUser);

        ArgumentCaptor<NewUser> captor = ArgumentCaptor.forClass(NewUser.class);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(savedUser)));

        verify(userService).createUser(captor.capture());

        assertEquals("name", captor.getValue().getName());
        assertEquals("email", captor.getValue().getEmail());
    }

    @Test
    @SneakyThrows
    @DisplayName("Вернуть 409 при попытке добавить пользователя с существующим email.")
    void createUser_whenDuplicatedEmail_thenThrowDuplicatedException() {
        NewUser newUser = new NewUser();
        newUser.setName("name");
        newUser.setEmail("test@mail.com");

        when(userService.createUser(any(NewUser.class))).thenThrow(new DuplicatedDataException(
                "Пользователь с почтой: " + newUser.getEmail() + " уже существует."
        ));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Пользователь с почтой: test@mail.com уже существует."));

        verify(userService).createUser(any(NewUser.class));
    }

    @Test
    @DisplayName("Обновить существующего пользователя.")
    @SneakyThrows
    void updateUser() {
        User savedUser = new User();
        savedUser.setEmail("email");
        savedUser.setName("updated name");
        savedUser.setId(1L);

        UpdateUser updateUser = new UpdateUser();
        updateUser.setName("updated name");

        when(userService.updateUser(eq(1L), any(UpdateUser.class))).thenReturn(savedUser);

        ArgumentCaptor<UpdateUser> captor = ArgumentCaptor.forClass(UpdateUser.class);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(savedUser)));

        verify(userService).updateUser(eq(1L), captor.capture());
        assertEquals("updated name", captor.getValue().getName());
        assertNull(captor.getValue().getEmail());
    }

    @Test
    @DisplayName("Найти пользователя.")
    @SneakyThrows
    void findUserById() {
        long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setName("name");
        user.setEmail("email");

        when(userService.getUser(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        verify(userService).getUser(userId);
    }

    @Test
    @DisplayName("Вернуть 404 если пользователь не найден.")
    @SneakyThrows
    void findUserById_whenWrongId_thenReturnNotFound() {
        long userId = 999L;

        when(userService.getUser(userId)).thenThrow(
                new NotFoundException("Пользователь с id: " + userId + " не найден."));

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Пользователь с id: 999 не найден."));

        verify(userService).getUser(userId);
    }

    @Test
    @DisplayName("Удалить пользователя.")
    @SneakyThrows
    void deleteUser() {
        long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(userId);
    }
}