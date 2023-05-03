package com.booking.laundry.laundrybooking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LaundryBookingDto {
    private long id;
    private long householdId;
    private long laundryRoomId;
    private LocalDate date;
    private int timeBlockId;
}
