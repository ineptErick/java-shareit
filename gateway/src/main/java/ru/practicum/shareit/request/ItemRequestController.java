package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestGatewayDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> add(
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody @Valid ItemRequestGatewayDto request) {
        log.info("Пользователь с userId={} запросил вещь: {}.", userId, request.getDescription());
        return requestClient.add(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getUsersRequests(
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пользователь с userId={} выгружает список запрошенных им вещей.", userId);
        return requestClient.getUsersRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests(
                                    @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                    @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(50) Integer size,
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пользователь с userId={} выгружает список всех запрошенных вещей. Параметры запроса: " +
                "from={}, size={}.", userId, from, size);
        return requestClient.getRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @Positive(message = "ID запроса должно быть > 0.")
                                    @PathVariable Long requestId) {
        log.info("Пользователь с userId={} выгружает запрос с requestId={}.", userId, requestId);
        return requestClient.getRequestById(userId, requestId);
    }
}
