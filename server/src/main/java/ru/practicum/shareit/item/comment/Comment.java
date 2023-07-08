package ru.practicum.shareit.item.comment;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String text;
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    @Column(name = "author_id", nullable = false)
    private Long authorId;
    @Column(nullable = false)
    private LocalDateTime created;
}
