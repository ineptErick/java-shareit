package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    private String name;
    @Email(groups = {Create.class, Update.class})
    @NotEmpty(groups = {Create.class})
    // В данном случае следует использовать не @NotNull,
    // а @NotEmpty, так как @Email допускает и пустую строку, за корректное значение)
    // - done
    private String email;
}
