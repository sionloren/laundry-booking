package com.booking.laundry.laundrybooking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeBlockDto {
    private int id;
    private LocalTime hourFrom;
    private LocalTime hourTo;
}
