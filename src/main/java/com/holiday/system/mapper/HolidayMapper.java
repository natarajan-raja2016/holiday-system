package com.holiday.system.mapper;

import com.holiday.system.dto.HolidayDTO;
import com.holiday.system.dto.HolidayResponse;
import com.holiday.system.model.Country;
import com.holiday.system.model.Holiday;
import org.springframework.stereotype.Component;

@Component
public class HolidayMapper {

    public HolidayDTO toDto(Holiday holiday) {
        HolidayDTO dto = new HolidayDTO();
        dto.setHoliday_name(holiday.getHolidayName());
        dto.setHoliday_date(holiday.getHolidayDate());
        dto.setCountry_code(holiday.getCountry().getCountryCode().toUpperCase());
        return dto;
    }

    public Holiday toEntity(HolidayDTO dto) {
        Holiday holiday = new Holiday();
        holiday.setHolidayName(dto.getHoliday_name());
        holiday.setHolidayDate(dto.getHoliday_date());
        holiday.setHolidayDow(dto.getHoliday_date().getDayOfWeek().name());
        Country country = new Country();
        country.setCountryCode(dto.getCountry_code().toUpperCase());
        country.setCountryName(dto.getCountry_name());
        holiday.setCountry(country);
        return holiday;
    }

}
