package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.user.dto.NewUser;
import ru.practicum.user.dto.UpdateUser;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Test
    @DisplayName("Добавить нового пользователя.")
    @SneakyThrows
    void createUser() {
        NewUser newUser = new NewUser();
        newUser.setEmail("name@email.com");
        newUser.setName("name");
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.CREATED).build();
        ArgumentCaptor<NewUser> captor = ArgumentCaptor.forClass(NewUser.class);

        when(userClient.createUser(any(NewUser.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated());

        verify(userClient).createUser(captor.capture());

        assertEquals("name", captor.getValue().getName());
        assertEquals("name@email.com", captor.getValue().getEmail());
    }

    @Test
    @DisplayName("Вернуть 400 если имя пользователя пустое.")
    @SneakyThrows
    void createUser_whenUserNameIsEmpty_thenThrowsException() {
        NewUser newUser = new NewUser();
        newUser.setName("  ");
        newUser.setEmail("name@email.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(NewUser.class));
    }

    @Test
    @DisplayName("Вернуть 400 если в имя пользователя подан null.")
    @SneakyThrows
    void createUser_whenUserNameIsNull_thenThrowsException() {
        NewUser newUser = new NewUser();
        newUser.setName(null);
        newUser.setEmail("name@email.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(NewUser.class));
    }

    @Test
    @DisplayName("Вернуть 400 если поле email пустое.")
    @SneakyThrows
    void createUser_whenUserMailIsEmpty_thenThrowsException() {
        NewUser newUser = new NewUser();
        newUser.setName("name");
        newUser.setEmail(" ");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(NewUser.class));
    }

    @Test
    @DisplayName("Вернуть 400 если в поле email подан null.")
    @SneakyThrows
    void createUser_whenUserMailIsNull_thenThrowsException() {
        NewUser newUser = new NewUser();
        newUser.setName("name");
        newUser.setEmail(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(NewUser.class));
    }

    @Test
    @DisplayName("Вернуть 400 если подан некорректный email.")
    @SneakyThrows
    void createUser_whenUserMailIsWrong_thenThrowsException() {
        NewUser newUser = new NewUser();
        newUser.setName("name");
        newUser.setEmail("email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(NewUser.class));
    }

    @Test
    @DisplayName("Обновить существующего пользователя.")
    @SneakyThrows
    void updateUser() {
        UpdateUser updateUser = new UpdateUser();
        updateUser.setName("updated name");
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).build();
        ArgumentCaptor<UpdateUser> captor = ArgumentCaptor.forClass(UpdateUser.class);

        when(userClient.updateUser(eq(1L), any(UpdateUser.class))).thenReturn(response);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk());

        verify(userClient).updateUser(eq(1L), captor.capture());
        assertEquals("updated name", captor.getValue().getName());
        assertNull(captor.getValue().getEmail());
    }

    @Test
    @DisplayName("Вернуть 400 если имя и email отсутствуют.")
    @SneakyThrows
    void updateUser_whenUserNameAndEmailIsNull_thenThrowException() {
        UpdateUser updateUser = new UpdateUser();

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(anyLong(), any(UpdateUser.class));
    }

    @Test
    @DisplayName("Вернуть 400 если подан не корректный email.")
    @SneakyThrows
    void updateUser_whenUserEmailIsWrong_thenThrowException() {
        UpdateUser updateUser = new UpdateUser();
        updateUser.setName("updated name");
        updateUser.setEmail("email");

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(anyLong(), any(UpdateUser.class));
    }

    @Test
    @DisplayName("Обновить пользователя, если передано только валидное имя (Email отсутствует).")
    @SneakyThrows
    void updateUser_whenOnlyEmailIsPresent_thenSuccess() {
        UpdateUser updateUser = new UpdateUser();
        updateUser.setEmail("correct@email.com");
        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(userClient.updateUser(eq(1L), any(UpdateUser.class))).thenReturn(response);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk());

        verify(userClient).updateUser(eq(1L), any(UpdateUser.class));
    }

    @Test
    @DisplayName("Вернуть 404, если бэкенд ответил, что пользователь не найден.")
    @SneakyThrows
    void updateUser_whenUserNotFoundInBackend_thenForward404() {
        UpdateUser updateUser = new UpdateUser();
        updateUser.setName("New Name");

        ResponseEntity<Object> backendError = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        when(userClient.updateUser(eq(1L), any(UpdateUser.class))).thenReturn(backendError);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Найти пользователя.")
    @SneakyThrows
    void findUserById() {
        long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).build();

        when(userClient.findUserById(userId)).thenReturn(response);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient).findUserById(userId);
    }

    @Test
    @DisplayName("Удалить пользователя.")
    @SneakyThrows
    void deleteUser() {
        long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userClient).deleteUser(userId);
    }

}