package com.booking.laundry.laundrybooking.controller;

import com.booking.laundry.laundrybooking.dto.LaundryBookingDto;
import com.booking.laundry.laundrybooking.dto.TimeBlockDto;
import com.booking.laundry.laundrybooking.exception.BookingNotAvailableException;
import com.booking.laundry.laundrybooking.exception.ResourceNotFoundException;
import com.booking.laundry.laundrybooking.service.LaundryBookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LaundryBookingController.class)
public class LaundryBookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LaundryBookingService service;

    @Test
    public void testGetAllLaundryBookingsForHousehold_withHousehold1() throws Exception {
        long householdId = 1;

        LaundryBookingDto laundryBookingDto_1 = new LaundryBookingDto(1L, 1L, 1L, LocalDate.parse("2023-05-01"), 1);
        LaundryBookingDto laundryBookingDto_2 = new LaundryBookingDto(2L, 1L, 1L, LocalDate.parse("2023-05-01"), 2);

        List<LaundryBookingDto> laundryBookingDtos = Arrays.asList(laundryBookingDto_1, laundryBookingDto_2);

        when(service.getLaundryBookingByHouseholdId(householdId)).thenReturn(laundryBookingDtos);
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/laundry-bookings/household/{id}", householdId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.[0].householdId").value(laundryBookingDto_1.getHouseholdId()))
                .andExpect(jsonPath("$.[0].timeBlockId").value(laundryBookingDto_1.getTimeBlockId()))
                .andExpect(jsonPath("$.[1].householdId").value(laundryBookingDto_2.getHouseholdId()))
                .andExpect(jsonPath("$.[1].timeBlockId").value(laundryBookingDto_2.getTimeBlockId()));
    }

    @Test
    public void testGetAllLaundryBookingsForHousehold_withNoBookings() throws Exception {
        long householdId = 1;
        String emptyArray = "[]";

        List<LaundryBookingDto> laundryBookingDtos = new ArrayList<>();

        when(service.getLaundryBookingByHouseholdId(householdId)).thenReturn(laundryBookingDtos);
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/laundry-bookings/household/{id}", householdId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(emptyArray));
    }

    @Test
    public void testGetAllLaundryBookingsForHousehold_withInvalidId() throws Exception {
        long householdId = 25L;
        String expectedErrorMessage = "Household not found with id : '25'";

        when(service.getLaundryBookingByHouseholdId(householdId)).thenThrow(new ResourceNotFoundException("Household", "id", householdId));

        mvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/laundry-bookings/household/{id}", householdId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals(expectedErrorMessage, result.getResolvedException().getMessage()));
    }

    @Test
    public void testCreateLaundryBooking_withValidInfo() throws Exception {
        LaundryBookingDto laundryBookingDto_1 = new LaundryBookingDto(1L, 2L, 2L, LocalDate.parse("2023-05-01"), 7);

        when(service.createLaundryBooking(any(LaundryBookingDto.class))).thenReturn(laundryBookingDto_1);
        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/laundry-bookings")
                        .content(asJsonString(laundryBookingDto_1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.householdId").value(laundryBookingDto_1.getHouseholdId()))
                .andExpect(jsonPath("$.laundryRoomId").value(laundryBookingDto_1.getLaundryRoomId()))
                .andExpect(jsonPath("$.date").value(laundryBookingDto_1.getDate().toString()))
                .andExpect(jsonPath("$.timeBlockId").value(laundryBookingDto_1.getTimeBlockId()));
    }

    @Test
    public void testCreateLaundryBooking_withTimeBlockAlreadyBooked() throws Exception {
        String expectedErrorMessage = "Booking not available on 2023-05-01 for time block id 7";

        LaundryBookingDto laundryBookingDto_1 = new LaundryBookingDto(1L, 2L, 2L, LocalDate.parse("2023-05-01"), 7);

        when(service.createLaundryBooking(any(LaundryBookingDto.class))).thenThrow(new BookingNotAvailableException(laundryBookingDto_1.getDate(), laundryBookingDto_1.getTimeBlockId()));
        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/laundry-bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(laundryBookingDto_1))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingNotAvailableException))
                .andExpect(result -> assertEquals(expectedErrorMessage, result.getResolvedException().getMessage()));
    }

    @Test
    public void testCreateLaundryBooking_withInvalidTimeBlockId() throws Exception {
        String expectedErrorMessage = "Time Block not found with id : '99'";
        int timeBlockId = 99;

        LaundryBookingDto laundryBookingDto_1 = new LaundryBookingDto(1L, 2L, 2L, LocalDate.parse("2023-05-01"), timeBlockId);

        when(service.createLaundryBooking(any(LaundryBookingDto.class))).thenThrow(new ResourceNotFoundException("Time Block", "id", timeBlockId));
        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/laundry-bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(laundryBookingDto_1))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals(expectedErrorMessage, result.getResolvedException().getMessage()));
    }

    @Test
    public void testCreateLaundryBooking_withInvalidHouseholdId() throws Exception {
        String expectedErrorMessage = "Household not found with id : '99'";
        long householdId = 99;

        LaundryBookingDto laundryBookingDto_1 = new LaundryBookingDto(1L, householdId, 2L, LocalDate.parse("2023-05-01"), 2);

        when(service.createLaundryBooking(any(LaundryBookingDto.class))).thenThrow(new ResourceNotFoundException("Household", "id", householdId));
        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/laundry-bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(laundryBookingDto_1))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals(expectedErrorMessage, result.getResolvedException().getMessage()));
    }

    @Test
    public void testCancelLaundryBookingById_withValidLaundryBookingId() throws Exception {
        String expectedErrorMessage = "Laundry Room not found with id : '99'";
        long laundryBookingId = 1;

        mvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/laundry-bookings/{id}", laundryBookingId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testCancelLaundryBookingById_withInvalidLaundryBookingId() throws Exception {
        String expectedErrorMessage = "Laundry Booking not found with id : '1'";
        long laundryBookingId = 1;

        doThrow(new ResourceNotFoundException("Laundry Booking", "id", laundryBookingId)).when(service).cancelLaundryBookingById(laundryBookingId);
        mvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/laundry-bookings/{id}", laundryBookingId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals(expectedErrorMessage, result.getResolvedException().getMessage()));
    }

    @Test
    public void testGetAvailableTimeBlocksByDate() throws Exception {
        TimeBlockDto timeBlockDto_1 = new TimeBlockDto(1, LocalTime.parse("09:00:00"), LocalTime.parse("10:00:00"));
        TimeBlockDto timeBlockDto_2 = new TimeBlockDto(2, LocalTime.parse("10:00:00"), LocalTime.parse("11:00:00"));

        List<TimeBlockDto> timeBlockDtos = Arrays.asList(timeBlockDto_1, timeBlockDto_2);

        when(service.getAvailableTimeBlocksByDate(any(LocalDate.class))).thenReturn(timeBlockDtos);
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/laundry-bookings/time-blocks/available")
                        .param("date", "2023-05-01"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));
    }

    @Test
    public void testGetAllBookedLaundryBookings() throws Exception {
        LaundryBookingDto laundryBookingDto_1 = new LaundryBookingDto(1L, 1L, 1L, LocalDate.parse("2023-05-01"), 1);
        LaundryBookingDto laundryBookingDto_2 = new LaundryBookingDto(2L, 1L, 1L, LocalDate.parse("2023-05-01"), 2);

        List<LaundryBookingDto> laundryBookingDtos = Arrays.asList(laundryBookingDto_1, laundryBookingDto_2);

        when(service.getAllBookedLaundryBookings()).thenReturn(laundryBookingDtos);
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/laundry-bookings")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.[0].householdId").value(laundryBookingDto_1.getHouseholdId()))
                .andExpect(jsonPath("$.[0].timeBlockId").value(laundryBookingDto_1.getTimeBlockId()))
                .andExpect(jsonPath("$.[1].householdId").value(laundryBookingDto_2.getHouseholdId()))
                .andExpect(jsonPath("$.[1].timeBlockId").value(laundryBookingDto_2.getTimeBlockId()));
    }

    @Test
    public void testGetAllBookedLaundryBookings_withNoBookings() throws Exception {
        String emptyArray = "[]";

        List<LaundryBookingDto> laundryBookingDtos = new ArrayList<>();

        when(service.getAllBookedLaundryBookings()).thenReturn(laundryBookingDtos);
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/laundry-bookings")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(emptyArray));
    }


    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            /*This is used so that we can convert LocalDate to string */
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
