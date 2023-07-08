package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class UserGatewayForUpdateDto {
    private String name;
    @Email(message = "Невалидная почта.")
    private String email;
}
