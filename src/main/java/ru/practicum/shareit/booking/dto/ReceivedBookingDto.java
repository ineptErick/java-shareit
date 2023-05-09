package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.user.dto.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ReceivedBookingDto {
    // Так же хорошо было бы использовать более декларативный подход
    // и навесить над полями start и end соответствующие аннотации валидации
    // - done

    // Также же над полем itemId следует навесить @NotNull,
    // так как бронирование должно быть передано с идентификатором события:
    @NotNull(groups = {Create.class})
    private Long itemId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)

    // Например, над start - @FutureOrPresent, так как дата начала бронирования может быть равна текущей,
    // либо же быть из будущего. Не забываем о @NotNull, так как первая аннотация допускает null:
    @FutureOrPresent(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private LocalDateTime start;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)

    // Аналогично для даты окончания, но в этом случае следует использовать @Future,
    // так как дата окончания должна быть после начала бронирования и не может быть ей равна:
    @Future(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private LocalDateTime end;
}
