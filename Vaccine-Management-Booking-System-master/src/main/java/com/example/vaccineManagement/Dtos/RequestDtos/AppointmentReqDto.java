package com.example.vaccineManagement.Dtos.RequestDtos;

import lombok.Data;
import java.time.LocalTime;
import java.util.Date;

@Data
public class AppointmentReqDto {

    private Integer docId;
    private Integer vaccineId;

    private Date appointmentDate;
    private LocalTime appointmentTime;
}
