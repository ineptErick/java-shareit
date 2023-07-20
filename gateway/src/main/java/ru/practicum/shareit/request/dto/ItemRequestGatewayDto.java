package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestGatewayDto {
    @NotBlank(message = "Описание запроса не может быть пустым.")
    private String description;
}
