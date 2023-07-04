package ru.practicum.shareit.util.validation;


import ru.practicum.shareit.booking.BookingIncomeDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndAfterStartValidator implements ConstraintValidator<EndAfterStartValidation, BookingIncomeDto> {
    @Override
    public boolean isValid(BookingIncomeDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        return bookingDto.getStart() != null &&
                bookingDto.getEnd() != null &&
                bookingDto.getStart().isBefore(bookingDto.getEnd());
    }
}
