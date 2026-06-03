package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    @DisplayName("Item: покрытие автосгенерированных методов Lombok для сущностей")
    void testEntitiesLombokMethods() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("some-item");

        Item item2 = new Item();
        item2.setId(1L);

        Item itemWithDifferentId = new Item();
        itemWithDifferentId.setId(2L);


        assertNotNull(item1.toString());
        assertEquals(item1, item2);
        assertNotEquals(item1, itemWithDifferentId);
        assertEquals(item1.hashCode(), item2.hashCode());
        assertNotEquals(null, item1);
    }

}