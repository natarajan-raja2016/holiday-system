package com.holiday.system.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country {

	@Id
	@Column(name = "country_code", length = 3, nullable = false)
	private String countryCode;

	@Column(name = "country_name", length = 30, nullable = false)
	private String countryName;

	public Country(String countryCode, String countryName) {
		this.countryCode = countryCode;
		this.countryName = countryName;
	}

	@OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Holiday> holidays;

}
