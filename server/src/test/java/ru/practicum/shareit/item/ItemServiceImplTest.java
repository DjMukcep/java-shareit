package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional(readOnly = true)
@SpringBootTest
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @Transactional
    @DisplayName("Добавить вещь: успешное сохранение вещи в базу.")
    void addItem() {
        User user = new User();
        user.setName("user name");
        user.setEmail("user@email.com");
        user = userRepository.save(user);
        Long userId = user.getId();
        NewItem newItem = new NewItem(
                "test-item-name",
                "desc",
                true,
                null
        );


        ItemDto itemDto = itemService.addItem(userId, newItem);
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow();

        assertEquals(itemDto.getId(), item.getId());
        assertEquals("test-item-name", item.getName());
        assertEquals("desc", item.getDescription());
        assertTrue(item.isAvailable());
        assertNull(item.getRequest());
        assertEquals("user name", item.getOwner().getName());
        assertEquals("user@email.com", item.getOwner().getEmail());
    }

    @Test
    @Transactional
    @DisplayName("Обновить вещь: успешное обновление данных вещи.")
    void updateItem() {
        User user = new User();
        user.setName("user name");
        user.setEmail("user@email.com");
        user = userRepository.save(user);

        Item item = Item.builder()
                .name("test-item-name")
                .description("desc")
                .isAvailable(false)
                .owner(user)
                .request(null)
                .build();
        item = itemRepository.save(item);

        UpdateItem updateItem = new UpdateItem(
                "new-test-item",
                "new-desc",
                true
        );


        ItemDto updatedItem = itemService.updateItem(user.getId(), item.getId(), updateItem);
        Item storedItem = itemRepository.findById(updatedItem.getId()).orElseThrow();

        assertEquals(updatedItem.getId(), storedItem.getId());
        assertEquals("new-test-item", storedItem.getName());
        assertEquals("new-desc", storedItem.getDescription());
        assertTrue(storedItem.isAvailable());
    }

    @Test
    @Transactional
    @DisplayName("Обновить вещь: 400 у пользователя нет прав на обновление данных о вещи.")
    void updateItem_whenUserIsNotOwner_thenThrowException() {
        User user = new User();
        user.setName("user name");
        user.setEmail("user@email.com");
        user = userRepository.save(user);

        User outsider = new User();
        outsider.setName("user2 name");
        outsider.setEmail("user2 email");
        outsider = userRepository.save(outsider);
        long outsiderIdId = outsider.getId();

        Item item = Item.builder()
                .name("test-item-name")
                .description("desc")
                .isAvailable(false)
                .owner(user) // user владелец
                .request(null)
                .build();
        item = itemRepository.save(item);
        long itemId = item.getId();

        UpdateItem updateItem = new UpdateItem(
                "new-test-item",
                "new-desc",
                true
        );

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.updateItem(outsiderIdId, itemId,updateItem));

        assertEquals(exception.getMessage(),
                String.format("Пользователь id:%d не является владельцем вещи id:%d", outsiderIdId, itemId));
    }

    @Test
    @DisplayName("Получить данные вещи из базы.")
    void getItem() {
        User user = new User();
        user.setName("user name");
        user.setEmail("user@email.com");
        user = userRepository.save(user);

        Item item = Item.builder()
                .name("test-item-name")
                .description("desc")
                .isAvailable(false)
                .owner(user)
                .request(null)
                .build();
        item = itemRepository.save(item);


        Item storedItem = itemService.getItem(item.getId());

        assertEquals(item.getId(), storedItem.getId());
        assertEquals("test-item-name", item.getName());
        assertEquals("desc", item.getDescription());
        assertFalse(storedItem.isAvailable());
        assertEquals("user name", storedItem.getOwner().getName());
        assertEquals("user@email.com", storedItem.getOwner().getEmail());
    }

    @Test
    @DisplayName("Получить список вещей пользователя.")
    void getItems() {
        User user = new User();
        user.setName("user name");
        user.setEmail("user@email.com");
        user = userRepository.save(user);

        Item item1 = Item.builder()
                .name("test-item-name1")
                .description("desc1")
                .isAvailable(false)
                .owner(user)
                .request(null)
                .build();
        item1 = itemRepository.save(item1);

        Item item2 = Item.builder()
                .name("test-item-name2")
                .description("desc2")
                .isAvailable(true)
                .owner(user)
                .request(null)
                .build();
        item2 = itemRepository.save(item2);


        List<Item> items = itemService.getItems(user.getId());

        assertEquals(2, items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertEquals(item2.getId(), items.get(1).getId());
        assertEquals("test-item-name1", items.get(0).getName());
        assertEquals("test-item-name2", items.get(1).getName());
        assertEquals("desc1", items.get(0).getDescription());
        assertEquals("desc2", items.get(1).getDescription());
    }

    @Test
    @DisplayName("Получить вещь вместе с комментариями.")
    void getItemWithComments() {
        User itemOwner = new User();
        itemOwner.setName("itemOwner");
        itemOwner.setEmail("itemOwner@email.com");
        itemOwner = userRepository.save(itemOwner);

        User booker1 = new User();
        booker1.setName("booker1");
        booker1.setEmail("booker1@email.com");
        booker1 = userRepository.save(booker1);

        User booker2 = new User();
        booker2.setName("booker2");
        booker2.setEmail("booker2@email.com");
        booker2 = userRepository.save(booker2);

        Item item = Item.builder()
                .name("test-item-name")
                .description("desc")
                .isAvailable(false)
                .owner(itemOwner)
                .request(null)
                .build();

        item = itemRepository.save(item);

        Comment comment1 = Comment.builder()
                .text("test-comment-1")
                .author(booker1)
                .item(item)
                .build();
        commentRepository.save(comment1);

        Comment comment2 = Comment.builder()
                .text("test-comment-2")
                .author(booker2)
                .item(item)
                .build();
        commentRepository.save(comment2);


        ItemWithComments itemWithComments = itemService.getItemWithComments(itemOwner.getId(), item.getId());

        assertEquals(2, itemWithComments.getComments().size());
        assertEquals("test-item-name", itemWithComments.getName());
        assertEquals("desc", itemWithComments.getDescription());
        assertFalse(itemWithComments.isAvailable());
        assertEquals("test-comment-1", itemWithComments.getComments().get(0).getText());
        assertEquals("test-comment-2", itemWithComments.getComments().get(1).getText());
    }

    @Test
    @DisplayName("Получить вещи пользователя вместе с комментариями.")
    void getItemsWithComments() {
        User itemOwner = new User();
        itemOwner.setName("itemOwner");
        itemOwner.setEmail("itemOwner@email.com");
        itemOwner = userRepository.save(itemOwner);

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@email.com");
        booker = userRepository.save(booker);

        Item item1 = Item.builder()
                .name("item1")
                .description("desc1")
                .isAvailable(true)
                .owner(itemOwner)
                .request(null)
                .build();
        item1 = itemRepository.save(item1);

        Item item2 = Item.builder()
                .name("item2")
                .description("desc2")
                .isAvailable(true)
                .owner(itemOwner)
                .request(null)
                .build();
        item2 = itemRepository.save(item2);

        Comment comment1 = Comment.builder()
                .text("test-comment-1")
                .author(booker)
                .item(item1)
                .build();
        commentRepository.save(comment1);

        Comment comment2 = Comment.builder()
                .text("test-comment-2")
                .author(booker)
                .item(item2)
                .build();
        commentRepository.save(comment2);


        List<ItemWithComments> withCommentsList = itemService.getItemsWithComments(itemOwner.getId());

        assertEquals(2, withCommentsList.size());
        assertEquals("item1", withCommentsList.get(0).getName());
        assertEquals("item2", withCommentsList.get(1).getName());
        assertEquals("desc1", withCommentsList.get(0).getDescription());
        assertEquals("desc2", withCommentsList.get(1).getDescription());
        assertEquals("test-comment-1", withCommentsList.get(0).getComments().getFirst().getText());
        assertEquals("test-comment-2", withCommentsList.get(1).getComments().getFirst().getText());
    }

    @Test
    @DisplayName("Найти вещь по описанию.")
    void searchItem() {
        User itemOwner = new User();
        itemOwner.setName("itemOwner");
        itemOwner.setEmail("itemOwner@email.com");
        itemOwner = userRepository.save(itemOwner);

        Item item1 = Item.builder()
                .name("item1")
                .description("desc1")
                .isAvailable(true)
                .owner(itemOwner)
                .request(null)
                .build();
        itemRepository.save(item1);

        Item item2 = Item.builder()
                .name("item2")
                .description("desc2")
                .isAvailable(true)
                .owner(itemOwner)
                .request(null)
                .build();
        itemRepository.save(item2);

        Item item3 = Item.builder()
                .name("item3")
                .description("desc1")
                .isAvailable(true)
                .owner(itemOwner)
                .request(null)
                .build();
        itemRepository.save(item3);


        List<ItemDto> dtoItemList = itemService.searchItem(itemOwner.getId(),"desc1");

        assertEquals(2, dtoItemList.size());
        assertEquals("item1", dtoItemList.get(0).getName());
        assertEquals("desc1", dtoItemList.get(0).getDescription());
        assertEquals("item3", dtoItemList.get(1).getName());
        assertEquals("desc1", dtoItemList.get(1).getDescription());
    }

    @Test
    @DisplayName("Найти вещь по описанию: вернуть пустой список если в поле поиска пришел null")
    void searchItem_whenRequestParamIsNull_thenReturnEmptyList() {
        User itemOwner = new User();
        itemOwner.setName("itemOwner");
        itemOwner.setEmail("itemOwner@email.com");
        itemOwner = userRepository.save(itemOwner);

        Item item = Item.builder()
                .name("item1")
                .description("desc1")
                .isAvailable(true)
                .owner(itemOwner)
                .request(null)
                .build();
        itemRepository.save(item);

        List<ItemDto> dtoItemList = itemService.searchItem(itemOwner.getId(),null);

        assertEquals(0, dtoItemList.size());
    }

    @Test
    @DisplayName("Найти вещь по описанию: вернуть пустой список если в поле поиска пустая строка")
    void searchItem_whenRequestParamIsEmpty_thenReturnEmptyList() {
        User itemOwner = new User();
        itemOwner.setName("itemOwner");
        itemOwner.setEmail("itemOwner@email.com");
        itemOwner = userRepository.save(itemOwner);

        Item item = Item.builder()
                .name("item1")
                .description("desc1")
                .isAvailable(true)
                .owner(itemOwner)
                .request(null)
                .build();
        itemRepository.save(item);

        List<ItemDto> dtoItemList = itemService.searchItem(itemOwner.getId()," ");

        assertEquals(0, dtoItemList.size());
    }

    @Test
    @Transactional
    @DisplayName("Добавить комметарий: успешное сохранение в базу.")
    void addComment() {
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@email.com");
        booker = userRepository.save(booker);

        User itemOwner = new User();
        itemOwner.setName("itemOwner");
        itemOwner.setEmail("itemOwner@email.com");
        itemOwner = userRepository.save(itemOwner);

        Item item = Item.builder()
                .name("test-item-name")
                .description("desc")
                .isAvailable(false)
                .owner(itemOwner)
                .request(null)
                .build();
        item = itemRepository.save(item);

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        booking = bookingRepository.save(booking);


        CommentDto commentDto = itemService.addComment(
                booker.getId(), item.getId(), new NewComment("test-comment"));

        assertNotNull(commentDto.getId());
        assertNotNull(commentDto.getCreated());
        assertEquals("test-comment", commentDto.getText());
        assertEquals("booker", commentDto.getAuthorName());
    }
}