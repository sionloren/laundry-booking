package com.booking.laundry.laundrybooking.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;

@Getter
@ResponseStatus(value = HttpStatus.OK)
public class BookingNotAvailableException extends RuntimeException {
    private LocalDate date;
    private int timeBlockId;

    public BookingNotAvailableException(LocalDate date, int timeBlockId) {
        /*Example: Booking not available on 2023-04-30 for time block id 1*/
        super(String.format("Booking not available on %s for time block id %s", date, timeBlockId));
        this.date = date;
        this.timeBlockId = timeBlockId;
    }
}
