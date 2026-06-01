package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {

    @Test
    @DisplayName("Проверка методов equals и hashCode для ItemRequest (100% покрытия)")
    void testEqualsAndHashCode() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("Description 1");
        request1.setCreatedAt(LocalDateTime.now());
        request1.setRequester(new User());

        ItemRequest request2 = new ItemRequest();
        request2.setId(1L); // тот же id

        ItemRequest request3 = new ItemRequest();
        request3.setId(2L);


        assertEquals(request1, request1);
        assertNotEquals(null, request1);
        assertNotEquals("ItemRequest", request1);
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);

        int hashCode1 = request1.hashCode();
        int hashCode2 = request2.hashCode();

        assertEquals(hashCode1, hashCode2);


        ItemRequest requestWithNullId1 = new ItemRequest();
        ItemRequest requestWithNullId2 = new ItemRequest();

        assertNotEquals(requestWithNullId1, requestWithNullId2);
    }
}