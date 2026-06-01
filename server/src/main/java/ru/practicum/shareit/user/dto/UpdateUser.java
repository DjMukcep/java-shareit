package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UpdateUser {
    private String name;
    private String email;

    public boolean hasUsername() {
        return !(name == null || name.isBlank());
    }

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }
}
