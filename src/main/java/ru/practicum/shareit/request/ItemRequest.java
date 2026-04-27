package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * И в пакете request будет ItemRequest — класс, отвечающий за запрос вещи. Его
 * атрибуты:
 * id — уникальный идентификатор запроса;
 * description — текст запроса, содержащий описание требуемой вещи;
 * requestor — пользователь, создавший запрос;
 * created — дата и время создания запроса.
 */
@Data
public class ItemRequest {
    private Long id;
    private String description;
    private User requester;
    private LocalDateTime createdAt;
}
