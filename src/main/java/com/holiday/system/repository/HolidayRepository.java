package com.holiday.system.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.holiday.system.model.Holiday;

import jakarta.transaction.Transactional;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

	@Modifying
	@Transactional
	@Query("DELETE FROM Holiday h WHERE h.country.countryCode = :countryCode AND h.holidayDate = :holidayDate")
	int deleteByCountryCodeAndHolidayDate(@Param("countryCode") String countryCode,
			@Param("holidayDate") LocalDate holidayDate);

	@Query("SELECT h FROM Holiday h WHERE h.country.countryName = :name AND h.holidayDate = :date")
	List<Holiday> findByCountryNameAndHolidayDate(String name, LocalDate date);

	@Query("SELECT h FROM Holiday h WHERE h.country.countryName = :name")
	List<Holiday> findByCountryName(String name);

	@Query("SELECT h FROM Holiday h JOIN FETCH h.country WHERE h.country.countryCode = :countryCode")
	List<Holiday> findByCountryCode(String countryCode);

	@Query("SELECT h FROM Holiday h JOIN FETCH h.country WHERE h.holidayName = :holidayName")
	List<Holiday> findByHolidayName(String holidayName);

	@Query("SELECT h FROM Holiday h JOIN FETCH h.country WHERE h.country.countryCode = :countryCode and h.holidayName = :holidayName")
	List<Holiday> findByCountryCodeAndHolidayName(String countryCode, String holidayName);

	Optional<Holiday> findByCountry_CountryCodeAndHolidayDate(String countryCode, LocalDate holidayDate);

	Optional<Holiday> findByCountry_CountryCodeAndHolidayNameIgnoreCase(String countryCode, String holidayName);
}
