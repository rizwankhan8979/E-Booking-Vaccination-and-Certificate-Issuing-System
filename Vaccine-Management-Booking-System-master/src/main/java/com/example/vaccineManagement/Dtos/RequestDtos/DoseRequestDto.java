package com.example.vaccineManagement.Dtos.RequestDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoseRequestDto {
    private int userId;
    private int vaccineId;
    private String vaccineName;
    private String batchNumber;
    private String vialNumber;
    private Integer docId; // ID of the doctor who administered the dose
}
