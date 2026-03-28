package com.example.vaccineManagement.Dtos.ResponseDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoseResponseDto {
    private String message;
    private int userId;
    private String userName;
    private String userEmail;
    private int doseNumber;
    private String batchNumber;
    private String vialNumber;
    private Date vaccinationDate;
}
