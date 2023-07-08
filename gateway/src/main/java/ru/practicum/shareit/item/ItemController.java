package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentGatewayDto;
import ru.practicum.shareit.item.dto.ItemGatewayDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@RestController

@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(
                                    @RequestBody @Valid ItemGatewayDto item,
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Пользователь с userId={} добавляет вещь. Название: {}.", userId, item.getName());
        return itemClient.addItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
                                    @RequestBody ItemGatewayDto itemGatewayDto,
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                    @Valid @Positive(message = "ID вещей должно быть > 0.")
                                    @PathVariable Long itemId) {
        log.info("Пользователь с userId={} обновляет вещь с itemId={}.", userId, itemId);
        return itemClient.updateItem(userId, itemGatewayDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
                                    @Valid @Positive(message = "ID вещей должно быть > 0.")
                                    @PathVariable Long itemId,
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Пользователь с userId={} выгружает вещь с itemId={}.", userId, itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsersItems(
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                    @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                    @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(50) Integer size) {
        log.info("Пользователь с userId={} выгружает список своих вещей. Параметры запроса: " +
                "from={}, size={}.", userId, from, size);
        return itemClient.getUsersItems(from, size, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(
                                    @RequestParam String text,
                                    @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                    @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(50) Integer size,
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пользователь с userId={} ищет вещи. Параметры запроса: " +
                "запрос:'{}', from={}, size={}.", userId, text, from, size);
        return itemClient.searchItem(from, size, userId, text);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItems() {
        log.info("Выгрузка всех вещей.");
        return itemClient.getAllItems();
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(
                                    @Valid @Positive(message = "ID вещей должно быть > 0.")
                                    @PathVariable Long itemId,
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Пользователь с userId={} удаляет вещь с itemId={}.", userId, itemId);
        return itemClient.deleteItem(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
                                    @RequestBody @Valid CommentGatewayDto commentGatewayDto,
                                    @Valid @Positive(message = "ID вещей должно быть > 0.")
                                    @PathVariable Long itemId,
                                    @Valid @Positive(message = "ID пользователя должен быть > 0.")
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Пользователь с userId={} оставляет комментарий к вещи с itemId={}.", userId, itemId);
        return itemClient.addComment(userId, itemId, commentGatewayDto);
    }
}
