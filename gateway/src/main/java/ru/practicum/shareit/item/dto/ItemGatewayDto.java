package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
public class ItemGatewayDto {
    @NotBlank(message = "Имя не может быть пустым.")
    private String name;
    @NotEmpty(message = "Описание не может быть пустым.")
    private String description;
    @NotNull
    private Boolean available;
    @Positive(message = "ID запроса должно быть > 0.")
    private Long requestId;
}