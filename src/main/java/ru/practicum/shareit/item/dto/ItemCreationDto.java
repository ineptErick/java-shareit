package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemCreationDto {
    // Также хорошо было бы разбить класс-DTO на несколько
    // - done
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;

    // Классы-DTO не должны содержать в себе сложные объекты по типу классов-сущностей.
    // Мы всегда должны контролировать те данные, что получаем и отдаем.
    // Не только в целях безопасности, но и производительности,
    // так как зачем тратить ресурсы на передачу тех данных, что не будут использоваться

}
