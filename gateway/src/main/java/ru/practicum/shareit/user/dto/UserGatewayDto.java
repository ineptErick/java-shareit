package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserGatewayDto {
    @NotBlank(message = "Имя не может быть пустым.")
    private String name;
    @Email(message = "Невалидная почта.")
    @NotBlank(message = "Почта не может быть пустой.")
    private String email;
}
