package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewComment;
import ru.practicum.shareit.user.User;


@UtilityClass
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(User user, Item item, NewComment comment) {
        return Comment.builder()
                .text(comment.getComment())
                .author(user)
                .item(item)
                .build();
    }
}
