package com.holiday.system.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.holiday.system.model.Country;
import com.holiday.system.model.Holiday;

@DataJpaTest
class HolidayRepositoryTest {

	@Autowired
	private HolidayRepository holidayRepository;

	@Autowired
	private CountryRepository countryRepository;

	private Country usa;
	private Holiday newYear;

	@BeforeEach
	void setUp() {
		usa = new Country("USA", "United States");
		countryRepository.save(usa);

		newYear = new Holiday("New Year", LocalDate.of(2025, 1, 1), "WEDNESDAY", usa);
		holidayRepository.save(newYear);
	}

	@Test
	void testFindByCountryCode() {
		List<Holiday> results = holidayRepository.findByCountryCode("USA");
		assertThat(results).hasSize(1);
		assertThat(results.get(0).getHolidayName()).isEqualTo("New Year");
	}

	@Test
	void testFindByCountryNameAndHolidayDate() {
		List<Holiday> results = holidayRepository.findByCountryNameAndHolidayDate("United States",
				LocalDate.of(2025, 1, 1));
		assertThat(results).isNotEmpty();
	}

	@Test
	void testFindByHolidayName() {
		List<Holiday> results = holidayRepository.findByHolidayName("New Year");
		assertThat(results).isNotEmpty();
	}

	@Test
	void testFindByCountryCodeAndHolidayName() {
		List<Holiday> results = holidayRepository.findByCountryCodeAndHolidayName("USA", "New Year");                                       
		assertThat(results).hasSize(1);
	}

	@Test
	void testFindByCountry_CountryCodeAndHolidayDate() {
		Optional<Holiday> result = holidayRepository.findByCountry_CountryCodeAndHolidayDate("USA",
				LocalDate.of(2025, 1, 1));
		assertThat(result).isPresent();
	}

	@Test
    void testDeleteByCountryCodeAndHolidayDate_WhenRecordExists() {
        Country country = new Country();
        country.setCountryCode("USA");
        country.setCountryName("United States");
        countryRepository.save(country);

        Holiday holiday = new Holiday();
        holiday.setHolidayDate(LocalDate.of(2025, 12, 25));
        holiday.setHolidayName("Christmas");
        holiday.setCountry(country);
        holiday.setHolidayDow("WED");
        holidayRepository.save(holiday);

        int deletedCount = holidayRepository.deleteByCountryCodeAndHolidayDate("USA", LocalDate.of(2025, 12, 25));

        assertEquals(1, deletedCount);
    }

    @Test
    void testDeleteByCountryCodeAndHolidayDate_WhenNoRecordExists() {
        int deletedCount = holidayRepository.deleteByCountryCodeAndHolidayDate("USA", LocalDate.of(2025, 12, 25));
        assertEquals(0, deletedCount);
    }
}
