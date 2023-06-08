package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {
    @ExceptionHandler(AlreadyUsedEmailException.class)
    public ResponseEntity<ErrorMessage> alreadyUsedEmail(AlreadyUsedEmailException e) {
        log.error("already used email: " + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage("Already used email:" + e.getMessage()));
    }

    @ExceptionHandler({InappropriateUserException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorMessage> notFound(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler({BadRequestException.class, ItemIsUnavailableException.class, BookingStatusAlreadySetException.class})
    public ResponseEntity<ErrorMessage> badRequest(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(UnsupportedStatusException.class)
    public ResponseEntity<ErrorResponseException> unsupportedStatus(UnsupportedStatusException e, HttpServletRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        String timestamp = formatter.format(ZonedDateTime.now());
        String path = request.getRequestURI();
        String error = e.getReasonPhrase();
        String message = e.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponseException errorResponse = new ErrorResponseException(timestamp, status.value(), error, message, path);
        return new ResponseEntity<>(errorResponse, status);
    }
}
