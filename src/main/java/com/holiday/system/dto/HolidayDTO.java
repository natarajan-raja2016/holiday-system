package com.holiday.system.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HolidayDTO {

	@NotEmpty(message = "country_code is mandatory.")
	private String country_code;
	@NotEmpty(message = "country_name is mandatory.")
	private String country_name;
	@NotNull(message = "holiday_date is mandatory.")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate holiday_date;
	@NotEmpty(message = "holiday_name is mandatory.")
	private String holiday_name;

}
