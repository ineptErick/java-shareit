package ru.practicum.shareit.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFoundException(final NotFoundException e) {
        return new ErrorResponse(
                "Объект не найден.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleModelValidationException(final ModelValidationException e) {
        return new ErrorResponse(
                "Ошибка в веденных данных.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleModelConflictException(final ModelConflictException e) {
        return new ErrorResponse(
                "Конфликт при создании объекта.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenConflictException(final ForbiddenException e) {
        return new ErrorResponse(
                "Обновление объекта не возможно.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingUnavailableException(final BookingUnavailableException e) {
        return new ErrorResponse(
                "Невозможно зарезервировать объект.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final BadRequest e) {
        return new ErrorResponse(
                e.getMessage(),
                e.getMessage());
    }
}
