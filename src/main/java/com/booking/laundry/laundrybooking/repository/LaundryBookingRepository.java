package com.booking.laundry.laundrybooking.repository;

import com.booking.laundry.laundrybooking.model.LaundryBooking;
import com.booking.laundry.laundrybooking.model.LaundryRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LaundryBookingRepository extends JpaRepository<LaundryBooking, Long> {
    Boolean existsByLaundryRoomAndBookingDateAndTimeBlockId(LaundryRoom laundryRoom, LocalDate date, int timeBlockId);
    List<LaundryBooking> findByHouseholdId(long householdId);
    List<LaundryBooking> findAllByBookingDate(LocalDate date);

}
