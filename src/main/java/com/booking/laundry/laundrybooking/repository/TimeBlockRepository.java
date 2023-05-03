package com.booking.laundry.laundrybooking.repository;

import com.booking.laundry.laundrybooking.model.TimeBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimeBlockRepository extends JpaRepository<TimeBlock, Long> {
    Boolean existsById(int timeBlockId);

    @Query("SELECT tb FROM TimeBlock tb WHERE tb.id NOT IN (:timeBlocks)")
    List<TimeBlock> findAllByTimeBlockNotIn(@Param("timeBlocks") List<Integer> timeBlocks);
}
