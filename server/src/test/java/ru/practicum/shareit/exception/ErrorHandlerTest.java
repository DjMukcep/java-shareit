package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ErrorHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    @DisplayName("handleNotFound: возврат 404 NOT_FOUND при NotFoundException")
    void handleNotFound() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"));
    }

    @Test
    @DisplayName("handleDuplicatedData: возврат 409 CONFLICT при DuplicatedDataException")
    void handleDuplicatedData() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Данные уже существуют"));
    }

    @Test
    @DisplayName("handleValidationError: возврат 400 BAD_REQUEST при ValidationException")
    void handleValidationError() throws Exception {
        mockMvc.perform(get("/test/validation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации данных"));
    }

    @Test
    @DisplayName("handleMissingHeader: возврат 400 BAD_REQUEST при отсутствии X-Sharer-User-Id")
    void handleMissingHeader() throws Exception {
        mockMvc.perform(get("/test/missing-header"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("handleOtherErrors: возврат 500 INTERNAL_SERVER_ERROR при непредвиденных Throwable")
    void handleOtherErrors() throws Exception {
        mockMvc.perform(get("/test/internal-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value(
                        "Произошла непредвиденная ошибка: Неизвестный сбой"));
    }

    @RestController
    private static class TestController {

        private static class LightTestException extends RuntimeException {
            public LightTestException(String message) {
                super(message, null, false, false);
            }
        }

        @GetMapping("/test/not-found")
        public void throwNotFound() {
            throw new NotFoundException("Объект не найден");
        }

        @GetMapping("/test/conflict")
        public void throwConflict() {
            throw new DuplicatedDataException("Данные уже существуют");
        }

        @GetMapping("/test/validation")
        public void throwValidation() {
            throw new ValidationException("Ошибка валидации данных");
        }

        @GetMapping("/test/missing-header")
        public void throwMissingHeader(@RequestHeader("X-Sharer-User-Id") Long userId) {
        }

        @GetMapping("/test/internal-error")
        public void throwRuntimeException() {
            throw new LightTestException("Неизвестный сбой");
        }
    }
}