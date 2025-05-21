package com.holiday.system.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.holiday.system.dto.HolidayDTO;
import com.holiday.system.model.Country;
import com.holiday.system.model.Holiday;

class HolidayMapperTest {

	private final HolidayMapper mapper = new HolidayMapper();

	@Test
	void testToDto() {
		Country country = new Country();
		country.setCountryCode("USD");
		country.setCountryName("United States");

		Holiday holiday = new Holiday();
		holiday.setHolidayName("New Year");
		holiday.setHolidayDate(LocalDate.of(2025, 1, 1));
		holiday.setHolidayDow("WEDNESDAY");
		holiday.setCountry(country);

		HolidayDTO dto = mapper.toDto(holiday);

		assertThat(dto).isNotNull();
		assertThat(dto.getHoliday_name()).isEqualTo("New Year");
		assertThat(dto.getHoliday_date()).isEqualTo(LocalDate.of(2025, 1, 1));
		assertThat(dto.getCountry_code()).isEqualTo("USD");
	}

	@Test
	void testToEntity() {
		HolidayDTO dto = new HolidayDTO();
		dto.setHoliday_name("Christmas");
		dto.setHoliday_date(LocalDate.of(2025, 12, 25));
		dto.setCountry_code("CAD");
		dto.setCountry_name("Canada");

		Holiday holiday = mapper.toEntity(dto);

		assertThat(holiday).isNotNull();
		assertThat(holiday.getHolidayName()).isEqualTo("Christmas");
		assertThat(holiday.getHolidayDate()).isEqualTo(LocalDate.of(2025, 12, 25));
		assertThat(holiday.getHolidayDow()).isEqualTo("THURSDAY");
		assertThat(holiday.getCountry()).isNotNull();
		assertThat(holiday.getCountry().getCountryCode()).isEqualTo("CAD");
		assertThat(holiday.getCountry().getCountryName()).isEqualTo("Canada");
	}
}
