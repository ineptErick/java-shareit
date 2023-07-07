package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "items")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @Column(name = "owner_id")
    @PositiveOrZero
    private Long owner;

    @Column(name = "request_id")
    private Long requestId;
}
