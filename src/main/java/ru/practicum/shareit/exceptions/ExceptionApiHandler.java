package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {
    // Форматтер следует вынести в поля класса в виде статической константы,
    // чтобы при каждом вызове метода не происходило создание нового объекта этого класса
    // - done
    static DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @ExceptionHandler(AlreadyUsedEmailException.class)
    public ResponseEntity<ErrorMessage> alreadyUsedEmail(AlreadyUsedEmailException e) {
        log.error("already used email: {}", e.getMessage());
        // При логгировании более эффективно использовать не конкатенацию,
        // а заполнитель(placeholder) {}
        // - done
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage("Already used email: " + e.getMessage()));
    }

    @ExceptionHandler({InappropriateUserException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorMessage> notFound(RuntimeException e) {
        // Отсутствует логгирование сообщения об ошибке
        // - done
        log.error("user not found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler({BadRequestException.class, ItemIsUnavailableException.class, BookingStatusAlreadySetException.class})
    public ResponseEntity<ErrorMessage> badRequest(RuntimeException e) {
        // Отсутствует логгирование сообщения об ошибке
        // - done
        log.error("already booked item: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(UnsupportedStatusException.class)
    public ResponseEntity<ErrorResponseException> unsupportedStatus(UnsupportedStatusException e, HttpServletRequest request) {
        // Отсутствует логгирование сообщения об ошибке
        // - done
        log.error("no such a status: {}", e.getMessage());
        String timestamp = formatter.format(ZonedDateTime.now());
        String path = request.getRequestURI();
        String error = e.getReasonPhrase();
        String message = e.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponseException errorResponseException = new ErrorResponseException(timestamp, status.value(), error, message, path);
        return new ResponseEntity<>(errorResponseException, status);
    }

    // Также следует добавить хендлер для обработки всех необработанных исключений,
    // ловить будем Exception, а возвращать статус 500 INTERNAL_SERVER_ERROR
    // - done
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleAllException(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(ex.getMessage()));
    }

    // Также следует добавить обработчик для исключения,
    // которое выбрасывается при валидации данных с помощью аннотаций - MethodArgumentNotValidException,
    // возвращать будет статус 400 BAD_REQUEST, так как ошибка связана с некорректными данными от пользователя
    // - done
    @ExceptionHandler({MethodArgumentNotValidException.class,}) //bd constraint handler
    public ResponseEntity<ErrorMessage> handleValidException(MethodArgumentNotValidException e) {
        log.error("validation error: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(e.getMessage()));
    }
}
