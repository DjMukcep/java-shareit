package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUser {
    private String name;
    @Email
    private String email;

    public boolean hasUsername() {
        return !(name == null || name.isBlank());
    }

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }
}
