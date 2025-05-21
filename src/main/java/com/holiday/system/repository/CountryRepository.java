package com.holiday.system.repository;

import com.holiday.system.model.Country;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, String> {

	Optional<Country> findByCountryCode(String countryCode);
    
	@Transactional
	int deleteByCountryCode(String countryCode);
}