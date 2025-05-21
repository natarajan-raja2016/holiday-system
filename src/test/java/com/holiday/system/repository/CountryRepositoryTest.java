package com.holiday.system.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.holiday.system.model.Country;

@DataJpaTest
class CountryRepositoryTest {

	@Autowired
	private CountryRepository countryRepository;

	@BeforeEach
	void setUp() {
		Country country = new Country();
		country.setCountryCode("USA");
		country.setCountryName("United States");
		countryRepository.save(country);
	}

	@Test
	void testFindByCountryCode() {
		Optional<Country> result = countryRepository.findByCountryCode("USA");
		assertThat(result).isPresent();
		assertThat(result.get().getCountryName()).isEqualTo("United States");
	}

	@Test
	void testFindByCountryCode_NotFound() {
		Optional<Country> result = countryRepository.findByCountryCode("CAD");
		assertThat(result).isNotPresent();
	}

	@Test
	void testSaveAndRetrieveCountry() {
		Country usd = new Country();
		usd.setCountryCode("USA");
		usd.setCountryName("America");
		countryRepository.save(usd);

		Optional<Country> retrieved = countryRepository.findById("USA");
		assertThat(retrieved).isPresent();
		assertThat(retrieved.get().getCountryName()).isEqualTo("America");
	}
}
