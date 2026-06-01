package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    @DisplayName("Booking: покрытие автосгенерированных методов Lombok для сущностей")
    void testEntitiesLombokMethods() {
        Booking booking1 = new Booking();
        booking1.setId(1L);

        Booking booking2 = new Booking();
        booking2.setId(1L);

        assertNotNull(booking1.toString());
        assertEquals(booking1, booking2);
        assertEquals(booking1.hashCode(), booking2.hashCode());
        assertNotEquals(null, booking1);
    }

}