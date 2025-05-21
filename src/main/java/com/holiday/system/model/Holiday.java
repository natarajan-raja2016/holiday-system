package com.holiday.system.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Holiday {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "holiday_name", length = 30, nullable = false)
	private String holidayName;

	@Column(name = "holiday_date", nullable = false)
	private LocalDate holidayDate;

	@Column(name = "holiday_dow", length = 10, nullable = false)
	private String holidayDow;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_code")
	@JsonIgnore
	private Country country;

	public Holiday(String holidayName, LocalDate holidayDate, Country country) {
		this.holidayName = holidayName;
		this.holidayDate = holidayDate;
		this.country = country;
	}

	public Holiday(String holidayName, LocalDate holidayDate, String holidayDow, Country country) {
		this.holidayName = holidayName;
		this.holidayDate = holidayDate;
		this.holidayDow = holidayDow;
		this.country = country;
	}
}
