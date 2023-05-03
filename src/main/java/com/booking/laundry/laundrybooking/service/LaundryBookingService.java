package com.booking.laundry.laundrybooking.service;

import com.booking.laundry.laundrybooking.dto.LaundryBookingDto;
import com.booking.laundry.laundrybooking.dto.TimeBlockDto;

import java.time.LocalDate;
import java.util.List;

public interface LaundryBookingService {
    LaundryBookingDto createLaundryBooking(LaundryBookingDto laundryBookingDto);
    void cancelLaundryBookingById(Long id);
    List<TimeBlockDto> getAvailableTimeBlocksByDate(LocalDate date);
    List<LaundryBookingDto> getAllBookedLaundryBookings();
    List<LaundryBookingDto> getLaundryBookingByHouseholdId(Long id);

}
