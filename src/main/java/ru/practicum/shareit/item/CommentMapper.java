package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewComment;
import ru.practicum.shareit.user.User;

import java.util.List;


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

    public static List<CommentDto> toCommentDto(List<Comment> comments) {
        return comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    public static Comment toComment(User user, Item item, NewComment comment) {
        return Comment.builder()
                .text(comment.getComment())
                .author(user)
                .item(item)
                .build();
    }
}
