package ru.practicum.shareit.item.comment;

public enum CommentMapper {
    INSTANT;

    public Comment toComment(CommentDto commentDto, Long itemId, Long authorId) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setItemId(itemId);
        comment.setAuthorId(authorId);
        comment.setCreated(commentDto.getCreated());
        return comment;
    }
}