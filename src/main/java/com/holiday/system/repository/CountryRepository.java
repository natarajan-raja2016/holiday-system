package com.holiday.system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.holiday.system.model.Country;

import jakarta.transaction.Transactional;

public interface CountryRepository extends JpaRepository<Country, String> {

	Optional<Country> findByCountryCode(String countryCode);

	@Transactional
	int deleteByCountryCode(String countryCode);
}