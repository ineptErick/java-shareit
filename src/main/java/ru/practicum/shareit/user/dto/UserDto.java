package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    // Так же и здесь, можно было бы использовать валидацию по группам ->
    private Long id;
    // Для имени:
    // @NotBlank(groups = {Create.class})
    // - done
    @NotBlank(groups = {Create.class})
    private String name;
    // Для почты аннотацию @Email следует учитываться для обновления и создания,
    // так как, если она не будет передана вообще, то все будет окей,
    // а если будет, тогда она будет проверена на соответствие почте, а вот аннотацию
    // @NotEmpty только для создания, Так как при обновлении передавать ее не обязательно
    // - done
    @Email(groups = {Create.class, Update.class})
    @NotNull(groups = {Create.class})
    private String email;
}
