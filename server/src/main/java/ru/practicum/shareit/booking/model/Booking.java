package ru.practicum.shareit.booking.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    @Column(name = "booker_id", nullable = false)
    private Long bookerId;
    @Column(nullable = false)
    private String status;

}
