package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("dbCommentRepository")
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT new ru.practicum.shareit.item.comment.CommentDto(c.id, c.text, u.name, c.created) " +
           "FROM Comment AS c " +
           "JOIN User AS u ON c.authorId = u.id " +
           "WHERE c.itemId = ?1")
    List<CommentDto> getAllByItemId(Long itemId);

}
