package com.holiday.system.dto;

import java.time.LocalDate;

import com.holiday.system.model.Country;

import lombok.Data;

@Data
public class HolidayResponse {

	private String name;
	private LocalDate date;
	private String dow;
	private Country country;

	public HolidayResponse(String name, LocalDate date, String dow, Country country) {
		this.name = name;
		this.date = date;
		this.dow = dow;
		this.country = new Country(country.getCountryCode(), country.getCountryName());
	}

	@Override
	public String toString() {
		return "HolidayResponse{Holiday Date : " + date.toString() + ", Holiday Name : " + name + ", Day of week : "
				+ dow + ", Country Code : " + country.getCountryCode() + ", Country name : " + country.getCountryName()
				+ "}";
	}
}
