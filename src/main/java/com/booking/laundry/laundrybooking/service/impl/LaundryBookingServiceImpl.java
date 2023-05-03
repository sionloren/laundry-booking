package com.booking.laundry.laundrybooking.service.impl;

import com.booking.laundry.laundrybooking.dto.LaundryBookingDto;
import com.booking.laundry.laundrybooking.dto.TimeBlockDto;
import com.booking.laundry.laundrybooking.exception.BookingNotAvailableException;
import com.booking.laundry.laundrybooking.exception.ResourceNotFoundException;
import com.booking.laundry.laundrybooking.model.Household;
import com.booking.laundry.laundrybooking.model.LaundryBooking;
import com.booking.laundry.laundrybooking.model.LaundryRoom;
import com.booking.laundry.laundrybooking.model.TimeBlock;
import com.booking.laundry.laundrybooking.repository.HouseholdRepository;
import com.booking.laundry.laundrybooking.repository.LaundryBookingRepository;
import com.booking.laundry.laundrybooking.repository.LaundryRoomRepository;
import com.booking.laundry.laundrybooking.repository.TimeBlockRepository;
import com.booking.laundry.laundrybooking.service.LaundryBookingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LaundryBookingServiceImpl implements LaundryBookingService {

    private LaundryBookingRepository laundryBookingRepository;
    private HouseholdRepository householdRepository;
    private LaundryRoomRepository laundryRoomRepository;
    private TimeBlockRepository timeBlockRepository;
    private ModelMapper mapper;

    public LaundryBookingServiceImpl() {
    }

    @Autowired
    public LaundryBookingServiceImpl(LaundryBookingRepository laundryBookingRepository,
                                     HouseholdRepository householdRepository,
                                     LaundryRoomRepository laundryRoomRepository,
                                     TimeBlockRepository timeBlockRepository,
                                     ModelMapper mapper) {
        this.laundryBookingRepository = laundryBookingRepository;
        this.householdRepository = householdRepository;
        this.laundryRoomRepository = laundryRoomRepository;
        this.timeBlockRepository = timeBlockRepository;
        this.mapper = mapper;
    }

    @Override
    public LaundryBookingDto createLaundryBooking(LaundryBookingDto laundryBookingDto) {
        LaundryBooking laundryBooking = mapToModel(laundryBookingDto);

        //check if schedule is available
        //call laundryBookingRepo exists by laundryRoom and bookingdate and timeblockid
        if(laundryBookingRepository.existsByLaundryRoomAndBookingDateAndTimeBlockId(laundryBooking.getLaundryRoom(),
                laundryBooking.getBookingDate(),
                laundryBooking.getTimeBlockId())) {
            throw new BookingNotAvailableException(laundryBookingDto.getDate(), laundryBookingDto.getTimeBlockId());
        }

        LaundryBooking savedLaundryBooking = laundryBookingRepository.save(laundryBooking);

        return mapToLaundryBookingDto(savedLaundryBooking);
    }

    private void validateTimeBlockId(LaundryBookingDto laundryBookingDto) {
        if(!timeBlockRepository.existsById(laundryBookingDto.getTimeBlockId())) {
            throw new ResourceNotFoundException("Time Block", "id", laundryBookingDto.getTimeBlockId());
        }
    }

    private void validateHouseholdId(long householdId) {
        if(!householdRepository.existsById(householdId)) {
            throw new ResourceNotFoundException("Household", "id", householdId);
        }
    }

    private Household getHouseholdByHouseholdId(long householdId) {
        return householdRepository.findById(householdId)
                .orElseThrow(() -> new ResourceNotFoundException("household", "id", householdId));
    }

    private LaundryRoom getLaundryRoomByLaundryRoomId(long laundryRoomId) {
        return laundryRoomRepository.findById(laundryRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Laundry Room", "id", laundryRoomId));
    }

    @Override
    public void cancelLaundryBookingById(Long id) {
        LaundryBooking laundryBooking = laundryBookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Laundry Booking", "id", id));

        laundryBookingRepository.delete(laundryBooking);
    }

    @Override
    public List<TimeBlockDto> getAvailableTimeBlocksByDate(LocalDate date) {
        List<LaundryBooking> allBookedLaundryBookings = laundryBookingRepository.findAllByBookingDate(date);

        List<Integer> allBookedTimeBlocks = allBookedLaundryBookings.stream()
                .map(LaundryBooking::getTimeBlockId)
                .collect(Collectors.toList());

        List<TimeBlock> timeBlocks = getAllAvailableTimeBlocks(allBookedTimeBlocks);

        return timeBlocks.stream()
                .map(timeBlock -> mapToTimeBlockDTO(timeBlock))
                .collect(Collectors.toList());
    }

    @Override
    public List<LaundryBookingDto> getAllBookedLaundryBookings() {
        List<LaundryBooking> laundryBookings = laundryBookingRepository.findAll();
        return laundryBookings.stream()
                .map(laundryBooking -> mapToLaundryBookingDto(laundryBooking))
                .collect(Collectors.toList());
    }

    private List<TimeBlock> getAllAvailableTimeBlocks(List<Integer> allBookedTimeBlocks) {
        return allBookedTimeBlocks.isEmpty() ? timeBlockRepository.findAll() : timeBlockRepository.findAllByTimeBlockNotIn(allBookedTimeBlocks);
    }

    @Override
    public List<LaundryBookingDto> getLaundryBookingByHouseholdId(Long id) {
        validateHouseholdId(id);

        List<LaundryBooking> laundryBookings = laundryBookingRepository.findByHouseholdId(id);
        List<LaundryBookingDto> laundryBookingDtos = laundryBookings.stream().map(laundryBooking -> mapToLaundryBookingDto(laundryBooking))
                .collect(Collectors.toList());

        return laundryBookingDtos;
    }

    private TimeBlockDto mapToTimeBlockDTO (TimeBlock timeBlock) {
        TimeBlockDto timeBlockDto = mapper.map(timeBlock, TimeBlockDto.class);
        return timeBlockDto;
    }

    private LaundryBookingDto mapToLaundryBookingDto(LaundryBooking laundryBooking) {
        LaundryBookingDto laundryBookingDto = mapper.map(laundryBooking, LaundryBookingDto.class);

        laundryBookingDto.setTimeBlockId(laundryBooking.getTimeBlockId());
        laundryBookingDto.setHouseholdId(laundryBooking.getHousehold().getId());
        laundryBookingDto.setLaundryRoomId(laundryBooking.getLaundryRoom().getId());

        return laundryBookingDto;
    }

    private LaundryBooking mapToModel(LaundryBookingDto laundryBookingDto) {
        validateTimeBlockId(laundryBookingDto);

        LaundryBooking laundryBooking = mapper.map(laundryBookingDto, LaundryBooking.class);

        Household household = getHouseholdByHouseholdId(laundryBookingDto.getHouseholdId());
        laundryBooking.setHousehold(household);

        LaundryRoom laundryRoom = getLaundryRoomByLaundryRoomId(laundryBookingDto.getLaundryRoomId());
        laundryBooking.setLaundryRoom(laundryRoom);

        return laundryBooking;
    }
}
