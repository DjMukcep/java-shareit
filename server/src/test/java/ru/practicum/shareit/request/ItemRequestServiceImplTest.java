package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional(readOnly = true)
@SpringBootTest
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;


    @Test
    @Transactional
    @DisplayName("Добавить запрос на вещь: успешное создание запроса.")
    void addRequest() {
        User user = new User();
        user.setName("some name");
        user.setEmail("some@email.com");
        user = userRepository.save(user);
        Long userId = user.getId();

        NewItemRequest newItemRequest = new NewItemRequest("some item");

        RequestDto itemRequest = itemRequestService.addRequest(newItemRequest,userId);

        assertEquals("some item", itemRequest.getDescription());
        assertNotNull(itemRequest.getId());
        assertNotNull(itemRequest.getCreatedAt());
    }

    @Test
    @DisplayName("Получить запрос: без связанных вещей.")
    void getRequest() {
        User user = new User();
        user.setName("some name");
        user.setEmail("some@email.com");
        user = userRepository.save(user);
        Long userId = user.getId();
        NewItemRequest newItemRequest = new NewItemRequest("some item");
        RequestDto itemRequest = itemRequestService.addRequest(newItemRequest,userId);
        Long requestId = itemRequest.getId();

        ItemRequestDto itemRequestDto = itemRequestService.getRequest(userId, requestId);

        assertEquals(requestId, itemRequestDto.getId());
        assertEquals("some item", itemRequestDto.getDescription());
        assertNotNull(itemRequestDto.getCreatedAt());
        assertTrue(itemRequestDto.getItems().isEmpty());
    }

    @Test
    @DisplayName("Получить запрос: успешное получение запроса.")
    void testGetRequest() {
        User user = new User();
        user.setName("some name");
        user.setEmail("some@email.com");
        user = userRepository.save(user);
        Long userId = user.getId();
        NewItemRequest newItemRequest = new NewItemRequest("some item");
        RequestDto itemRequest = itemRequestService.addRequest(newItemRequest,userId);
        Long requestId = itemRequest.getId();

        ItemRequest request = itemRequestService.getRequest(requestId);

        assertEquals(requestId, request.getId());
        assertEquals("some item", request.getDescription());
        assertEquals("some name",request.getRequester().getName());
        assertNotNull(request.getCreatedAt());
    }

    @Test
    @DisplayName("Получить запросы всех других пользователей.")
    void getRequests() {
        User user = new User();
        user.setName("some name");
        user.setEmail("some@email.com");
        user = userRepository.save(user);
        Long userId = user.getId();

        User anotherUser = new User();
        anotherUser.setName("some another name");
        anotherUser.setEmail("someanother@email.com");
        anotherUser = userRepository.save(anotherUser);
        Long anotherUserId = anotherUser.getId();

        NewItemRequest newItemRequest1 = new NewItemRequest("some item1");
        NewItemRequest newItemRequest2 = new NewItemRequest("some item2");

        itemRequestService.addRequest(newItemRequest1,userId);
        itemRequestService.addRequest(newItemRequest2,userId);

        List<RequestDto> requests = itemRequestService.getRequests(anotherUserId);

        assertEquals(2, requests.size());
        assertEquals("some item1", requests.getLast().getDescription());
        assertEquals("some item2", requests.getFirst().getDescription());
    }

    @Test
    @DisplayName("Получить запросы конкретного пользователя.")
    void getUserRequests() {
        User user = new User();
        user.setName("some name");
        user.setEmail("some@email.com");
        user = userRepository.save(user);
        Long userId = user.getId();

        NewItemRequest newItemRequest1 = new NewItemRequest("some item1");
        NewItemRequest newItemRequest2 = new NewItemRequest("some item2");

        itemRequestService.addRequest(newItemRequest1,userId);
        itemRequestService.addRequest(newItemRequest2,userId);

        List<ItemRequestDto> requests = itemRequestService.getUserRequests(userId);

        assertEquals(2, requests.size());
        assertEquals("some item1", requests.getLast().getDescription());
        assertEquals("some item2", requests.getFirst().getDescription());
    }
}