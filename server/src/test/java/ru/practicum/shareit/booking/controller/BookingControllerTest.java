package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    BookingService bookingService;
    @MockBean
    UserService userService;
    @MockBean
    ItemService itemService;

    @Test
    void addBooking() throws Exception {
        Long bookerId = 0L;
        Booking booking = new Booking();
        booking.setId(0L);
        booking.setBookerId(bookerId);
        booking.setStatus(String.valueOf(BookingState.WAITING));
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItemId(0L);

        Long itemId = 0L;
        ItemBookingDto itemBookingDto = new ItemBookingDto(itemId, "Item");
        UserBookingDto userBookingDto = new UserBookingDto(bookerId);
        BookingDto bookingDto = BookingMapper.INSTANT.toBookingDto(booking, itemBookingDto, userBookingDto);


        Mockito.when(bookingService.saveBooking(booking, bookerId))
                .thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings", bookerId, bookingDto)
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void updateItem() throws Exception {
        Long ownerId = 0L;
        Long bookingId = 0L;

        Long bookerId = 0L;
        Booking booking = new Booking();
        booking.setId(0L);
        booking.setBookerId(bookerId);
        booking.setStatus(String.valueOf(BookingState.WAITING));
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItemId(0L);

        Long itemId = 0L;
        ItemBookingDto itemBookingDto = new ItemBookingDto(itemId, "Item");
        UserBookingDto userBookingDto = new UserBookingDto(bookerId);
        BookingDto bookingDto = BookingMapper.INSTANT.toBookingDto(booking, itemBookingDto, userBookingDto);

        Mockito.when(bookingService.acceptBooking(bookingId, ownerId, true)).thenReturn(bookingDto);
        String result = mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", "true")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Assertions.assertEquals(objectMapper.writeValueAsString(bookingDto), result);
        Mockito.verify(bookingService).acceptBooking(bookingId, ownerId, true);
    }

    @Test
    void getAllBooking() throws Exception {
        Long bookerId = 0L;
        Long bookingId = 0L;
        List<Booking> bookingDtos = List.of();

        Mockito.when(bookingService.getAllItems()).thenReturn(bookingDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/all", bookingId)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bookingService).getAllItems();
    }

    @Test
    void getAllUsersBookings() throws Exception {
        Long bookerId = 0L;
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<BookingDto> bookingDtos = List.of();

        User user = new User();
        Long ownerId = 0L;
        user.setId(ownerId);
        user.setName("User");
        user.setEmail("user@meil.ru");

        UserDto userDto = UserMapper.INSTANT.toUserDto(user);

        Mockito.when(userService.getUserById(bookerId)).thenReturn(userDto);
        Mockito.when(bookingService.getBookingByState(bookerId, BookingState.REJECTED.toString(), pageRequest, false)).thenReturn(bookingDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", "REJECTED")
                        .param("size", "10")
                        .param("from", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bookingService).getBookingByState(bookerId, BookingState.REJECTED.toString(), pageRequest, false);
    }

    @Test
    void getAllOwnerBookings() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10);

        User user = new User();
        Long ownerId = 0L;
        user.setId(ownerId);
        user.setName("User");
        user.setEmail("user@meil.ru");

        UserDto userDto = UserMapper.INSTANT.toUserDto(user);

        List<BookingDto> bookingDtos = List.of();
        Mockito.when(userService.getUserById(ownerId)).thenReturn(userDto);
        Mockito.when(bookingService.getBookingByState(ownerId, BookingState.REJECTED.toString(), pageRequest, true)).thenReturn(bookingDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "REJECTED")
                        .param("size", "10")
                        .param("from", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bookingService).getBookingByState(ownerId, BookingState.REJECTED.toString(), pageRequest, true);
    }

    @Test
    void getBooking() throws Exception {
        Long bookingId = 0L;

        Long bookerId = 0L;
        Booking booking = new Booking();
        booking.setId(0L);
        booking.setBookerId(bookerId);
        booking.setStatus(String.valueOf(BookingState.WAITING));
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItemId(0L);

        Long itemId = 0L;
        ItemBookingDto itemBookingDto = new ItemBookingDto(itemId, "Item");
        UserBookingDto userBookingDto = new UserBookingDto(bookerId);
        BookingDto bookingDto = BookingMapper.INSTANT.toBookingDto(booking, itemBookingDto, userBookingDto);

        Mockito.when(bookingService.getBooking(bookingId, bookerId)).thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bookingService).getBooking(bookingId, bookerId);
    }
}