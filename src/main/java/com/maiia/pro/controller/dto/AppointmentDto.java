package com.maiia.pro.controller.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDto {

    private Integer patientId;
    private Integer practitionerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
