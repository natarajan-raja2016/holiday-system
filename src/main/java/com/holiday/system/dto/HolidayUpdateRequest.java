package com.holiday.system.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class HolidayUpdateRequest {

    @NotNull(message = "holiday_date is mandatory.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate holiday_date;
    @NotEmpty(message = "holiday_name is mandatory.")
    private String holiday_name;

}
