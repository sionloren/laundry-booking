package com.booking.laundry.laundrybooking.controller;

import com.booking.laundry.laundrybooking.dto.LaundryBookingDto;
import com.booking.laundry.laundrybooking.dto.TimeBlockDto;
import com.booking.laundry.laundrybooking.service.LaundryBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/laundry-bookings")
public class LaundryBookingController {
    private LaundryBookingService bookingService;

    @Autowired
    public LaundryBookingController(LaundryBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<LaundryBookingDto> createLaundryBooking(@RequestBody LaundryBookingDto laundryBookingDto) {
        return new ResponseEntity<>(bookingService.createLaundryBooking(laundryBookingDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LaundryBookingDto>> getAllBookedLaundryBookings() {
        return new ResponseEntity<>(bookingService.getAllBookedLaundryBookings(), HttpStatus.OK);
    }

    @GetMapping("/household/{id}")
    public ResponseEntity<List<LaundryBookingDto>> getLaundryBookingByHouseholdId(@PathVariable(name="id") Long id) {
        return new ResponseEntity<>(bookingService.getLaundryBookingByHouseholdId(id), HttpStatus.OK);
    }

    @GetMapping("/time-blocks/available")
    public ResponseEntity<List<TimeBlockDto>> getAvailableLaundryBookingByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return new ResponseEntity<>(bookingService.getAvailableTimeBlocksByDate(date), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelLaundryBookingById(@PathVariable(name="id") Long id) {
        bookingService.cancelLaundryBookingById(id);
        return ResponseEntity.ok("Laundry booking deleted successfully");
    }





}
