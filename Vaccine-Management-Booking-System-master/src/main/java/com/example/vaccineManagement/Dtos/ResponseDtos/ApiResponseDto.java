package com.example.vaccineManagement.Dtos.ResponseDtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponseDto {

    private boolean success;
    private String message;
}