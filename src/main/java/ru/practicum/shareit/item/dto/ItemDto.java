package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ItemDto {
    int id;
    int owner;

    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
}
