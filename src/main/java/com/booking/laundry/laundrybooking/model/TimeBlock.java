package com.booking.laundry.laundrybooking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(
        name = "time_blocks"
)
public class TimeBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(columnDefinition = "TIME")
    private LocalTime hourFrom;
    @Column(columnDefinition = "TIME")
    private LocalTime hourTo;
}
