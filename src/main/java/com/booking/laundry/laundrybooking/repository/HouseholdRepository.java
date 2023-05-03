package com.booking.laundry.laundrybooking.repository;

import com.booking.laundry.laundrybooking.model.Household;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseholdRepository extends JpaRepository<Household, Long> {
}
