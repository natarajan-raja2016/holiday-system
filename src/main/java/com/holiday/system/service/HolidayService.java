package com.holiday.system.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.holiday.system.dto.HolidayDTO;
import com.holiday.system.dto.HolidayResponse;
import com.holiday.system.dto.HolidayUpdateRequest;
import com.holiday.system.exception.ResourceNotFoundException;
import com.holiday.system.mapper.HolidayMapper;
import com.holiday.system.model.Country;
import com.holiday.system.model.Holiday;
import com.holiday.system.repository.CountryRepository;
import com.holiday.system.repository.HolidayRepository;
import com.holiday.system.util.Misc;

@Service
public class HolidayService {

	private static final Logger log = LoggerFactory.getLogger(HolidayService.class);

	private final HolidayRepository holidayRepository;
	private final CountryRepository countryRepository;
	private final HolidayMapper holidayMapper;

	public HolidayService(HolidayRepository holidayRepository, CountryRepository countryRepository,
			HolidayMapper holidayMapper) {
		this.holidayRepository = holidayRepository;
		this.countryRepository = countryRepository;
		this.holidayMapper = holidayMapper;
	}

	/**
	 * To add holiday entry
	 * @param holidayDTO DTO object
	 * @return added Holiday bean
	 * @throws IllegalStateException throw if holiday exist already
	 */
	public Holiday addHoliday(HolidayDTO holidayDTO) throws IllegalStateException {
		Optional<Holiday> holidayOptional = holidayRepository.findByCountry_CountryCodeAndHolidayDate(
				holidayDTO.getCountry_code().toUpperCase(), holidayDTO.getHoliday_date());

		holidayOptional.ifPresent(h -> {
			throw new IllegalStateException("Holiday already exists : " + h.getCountry().getCountryCode() + " : "
					+ h.getHolidayDate() + " : " + h.getHolidayName());
		});

		countryRepository.findByCountryCode(holidayDTO.getCountry_code().toUpperCase()).orElseGet(() -> {
			Country newCountry = new Country();
			newCountry.setCountryCode(holidayDTO.getCountry_code().toUpperCase());
			newCountry.setCountryName(holidayDTO.getCountry_name());
			return countryRepository.save(newCountry);
		});

		Holiday holiday = holidayMapper.toEntity(holidayDTO);

		return holidayRepository.save(holiday);
	}

	/**
	 * To update holiday using country code and holiday date
	 * @param countryCode  country code should be ISO3 country code
	 * @param holidayDate Accept date in ISO format and it limits to current year
	 * @param updateRequest DTO object to receive the Request body input
	 * @return response in DTO object
	 */
	public HolidayResponse updateHolidayByCodeAndDate(String countryCode, LocalDate holidayDate,
			HolidayUpdateRequest updateRequest) {
		Holiday holiday = holidayRepository
				.findByCountry_CountryCodeAndHolidayDate(countryCode.toUpperCase(), holidayDate)
				.orElseThrow(() -> new ResourceNotFoundException(String
						.format("No holiday found for country code '%s' on date '%s'.", countryCode, holidayDate)));

		holiday.setHolidayDate(updateRequest.getHoliday_date());
		holiday.setHolidayDow(updateRequest.getHoliday_date().getDayOfWeek().name());
		holiday.setHolidayName(updateRequest.getHoliday_name());

		Holiday updatedHoliday = holidayRepository.save(holiday);
		return new HolidayResponse(updatedHoliday.getHolidayName(), updatedHoliday.getHolidayDate(),
				updatedHoliday.getHolidayDow(), updatedHoliday.getCountry());

	}

	/**
	 * To update holiday by country code and holiday name
	 * @param countryCode country code should be ISO3 country code
	 * @param holidayName holiday name input from user
	 * @param updateRequest DTO object to receive and update user input
	 * @return updated holiday entry
	 */
	public HolidayResponse updateHolidayByCodeAndName(String countryCode, String holidayName,
			HolidayUpdateRequest updateRequest) {
		Holiday holiday = holidayRepository
				.findByCountry_CountryCodeAndHolidayNameIgnoreCase(countryCode.toUpperCase(), holidayName)
				.orElseThrow(() -> new ResourceNotFoundException(String
						.format("No holiday found for country code '%s' on date '%s'.", countryCode, holidayName)));

		holiday.setHolidayDate(updateRequest.getHoliday_date());
		holiday.setHolidayDow(updateRequest.getHoliday_date().getDayOfWeek().name());
		holiday.setHolidayName(updateRequest.getHoliday_name());

		Holiday updatedHoliday = holidayRepository.save(holiday);
		return new HolidayResponse(updatedHoliday.getHolidayName(), updatedHoliday.getHolidayDate(),
				updatedHoliday.getHolidayDow(), updatedHoliday.getCountry());

	}

	/**
	 * To return all holiday entries
	 * @return list of holiday entries
	 */
	public List<HolidayDTO> getAll() {
		List<Holiday> holidayList = holidayRepository.findAll();
		return holidayList.stream().map(holiday -> new HolidayDTO(
				holiday.getCountry().getCountryCode(), holiday.getCountry().getCountryName(), holiday.getHolidayDate(),
				holiday.getHolidayName())).collect(Collectors.toList());
	}

	/**
	 * To search holidays using country code
	 * @param countryCode ISO3 country code
	 * @return list of holidays
	 */
	public List<HolidayDTO> searchByCountryCode(String countryCode) {
		List<Holiday> holidayList = holidayRepository.findByCountryCode(countryCode.toUpperCase());
		return holidayList.stream().map(holiday -> new HolidayDTO(

				holiday.getCountry().getCountryCode(), holiday.getCountry().getCountryName(), holiday.getHolidayDate(),
				holiday.getHolidayName())).collect(Collectors.toList());
	}

	/**
	 * To search holidays using country code and holiday name
	 * @param countryCode ISO3 country code
	 * @param holidayName holiday name
	 * @return list of holidays
	 */
	public List<HolidayDTO> searchByCountryCodeAndHolidayName(String countryCode, String holidayName) {
		List<Holiday> holidayList = holidayRepository.findByCountryCodeAndHolidayName(countryCode.toUpperCase(),
				holidayName);

		return holidayList.stream().map(holiday -> new HolidayDTO(
				holiday.getCountry().getCountryCode(), holiday.getCountry().getCountryName(), holiday.getHolidayDate(),
				holiday.getHolidayName())).collect(Collectors.toList());
	}

	/**
	 * To delete holidays using country code
	 * @param countryCode ISO3 country code
	 * @return deleted count
	 */
	public long deleteHolidaysByCountryCode(String countryCode) {
		long deletedCount = countryRepository.deleteByCountryCode(countryCode.toUpperCase());
		log.info("deleteHolidaysByCountryCode() - Deleted {} records for countryCode: {}", deletedCount, countryCode);
		return deletedCount;
	}

	/**
	 * To delete holidays based on country code and holiday date
	 * @param countryCode ISO3 code
	 * @param holidayDate ISO date
	 * @return deleted count
	 */
	public long deleteByCountryCodeAndDate(String countryCode, LocalDate holidayDate) {
		long deletedCount = holidayRepository.deleteByCountryCodeAndHolidayDate(countryCode, holidayDate);
		log.info("Deleted {} holiday record(s) for countryCode={} on date={}", deletedCount, countryCode, holidayDate);
		return deletedCount;
	}

	/**
	 * To parse and save Csv file
	 * @param file Csv file
	 * @return file process status with count details
	 * @throws Exception when it occurs while process Csv file
	 */
	public String parseAndSaveCsv(MultipartFile file) throws Exception {
		long insertedCount = 0;
		long rejectedCount = 0;
		StringBuilder rejectedDetails = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

			CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader().setIgnoreHeaderCase(true).setTrim(true)
					.build();

			try (CSVParser csvParser = new CSVParser(reader, csvFormat)) {

				List<String> expectedHeaders = List.of("country_code", "country_name", "holiday_date", "holiday_name");

				Map<String, Integer> actualHeaderMap = csvParser.getHeaderMap();
				List<String> actualHeadersLower = actualHeaderMap.keySet().stream().map(String::toLowerCase).toList();

				List<String> missingHeaders = expectedHeaders.stream().filter(header -> !actualHeadersLower.contains(header))
						.toList();

				if (!missingHeaders.isEmpty()) {
					throw new IllegalArgumentException("Invalid CSV headers and Missing columns : " + missingHeaders);
				}

				String holidayName, holidayDateStr, countryCode, countryName;
				LocalDate holidayDate;
				HolidayDTO holidayDTO;

				for (CSVRecord csvRecord : csvParser) {
					holidayName = csvRecord.get("holiday_name");
					holidayDateStr = csvRecord.get("holiday_date");
					countryCode = csvRecord.get("country_code").toUpperCase();
					countryName = csvRecord.get("country_name");

					if (!StringUtils.hasText(holidayName) || !StringUtils.hasText(holidayDateStr)
							|| !StringUtils.hasText(countryCode) || !StringUtils.hasText(countryName)) {
						rejectedDetails.append("Row ").append((csvRecord.getRecordNumber()+1)).append(" - mandatory field(s) are empty so ignored.").append(System.lineSeparator());
						rejectedCount++;
						continue;
					}

					holidayDate = Misc.isValidDateFormat(holidayDateStr);
					if (holidayDate == null) {
						rejectedDetails.append("Row ").append((csvRecord.getRecordNumber()+1)).append(" - Invalid Date format so ignored").append(System.lineSeparator());
						rejectedCount++;
						continue;
					}

					if (!Misc.isDateInCurrentYear(holidayDate)) {
						rejectedDetails.append("Row ").append((csvRecord.getRecordNumber()+1)).append(" - Holiday date should be current year so ignored").append(System.lineSeparator());
						rejectedCount++;
						continue;
					}

					if (!Misc.isISO3CountryCode(countryCode)) {
						rejectedDetails.append("Row ").append((csvRecord.getRecordNumber()+1)).append(" - Country code should be ISO3 Country code so ignored").append(System.lineSeparator());
						rejectedCount++;
						continue;
					}

					holidayDTO = new HolidayDTO();
					holidayDTO.setHoliday_date(holidayDate);
					holidayDTO.setHoliday_name(holidayName);
					holidayDTO.setCountry_code(countryCode);
					holidayDTO.setCountry_name(countryName);

					try {
						addHoliday(holidayDTO);
					}catch (IllegalStateException ie){
						rejectedDetails.append("Row ").append((csvRecord.getRecordNumber()+1)).append(" - ").append(ie.getMessage()).append(System.lineSeparator());
						rejectedCount++;
						continue;
					}
					insertedCount++;
				}
			}

			StringBuilder finalOutput = new StringBuilder();
			long total = insertedCount + rejectedCount;
			finalOutput.append("Total number of records processed : ").append(total).append(System.lineSeparator());
			finalOutput.append("Number of successfully records : ").append(insertedCount).append(System.lineSeparator());
			finalOutput.append("Number of rejected records : ").append(rejectedCount).append(System.lineSeparator());
			finalOutput.append(rejectedDetails.toString());
			return finalOutput.toString();
		}
	}
}