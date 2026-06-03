package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    @DisplayName("Comment: покрытие автосгенерированных методов Lombok для сущностей")
    void testEntitiesLombokMethods() {
        Comment comment1 = new Comment();
        comment1.setId(1L);

        Comment comment2 = new Comment();
        comment2.setId(1L);

        Comment comment3 = new Comment();


        assertNotNull(comment1.toString());
        assertEquals(comment1, comment2);
        assertNotEquals(comment1, comment3);
        assertEquals(comment1.hashCode(), comment2.hashCode());
    }

}