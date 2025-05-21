package com.holiday.system.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.holiday.system.dto.HolidayDTO;
import com.holiday.system.dto.HolidayResponse;
import com.holiday.system.dto.HolidayUpdateRequest;
import com.holiday.system.exception.MandatoryFieldException;
import com.holiday.system.service.HolidayService;
import com.holiday.system.util.Misc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/holidays")
public class HolidayController {

	private static final Logger log = LoggerFactory.getLogger(HolidayController.class);

	private final HolidayService holidayService;

	public HolidayController(HolidayService holidayService) {
		this.holidayService = holidayService;
	}

	/**
	 * To add Holiday entry
	 * @param holidayDTO - DTO object carry the required attributes for holiday entry
	 * @return - the status of add end point
	 */
	@PostMapping("/add")
	public ResponseEntity<String> addHoliday(@Valid @RequestBody HolidayDTO holidayDTO) {

		LocalDate holidate = holidayDTO.getHoliday_date();
		if (!Misc.isDateInCurrentYear(holidate)) {
			return ResponseEntity.badRequest().body("Holiday date should be current year.");
		}

		String countryCode = holidayDTO.getCountry_code();
		if (!Misc.isISO3CountryCode(countryCode)) {
			return ResponseEntity.badRequest().body("Country code should be valid ISO3 Country code.");
		}

		log.info("addHoliday() - holiday: {} ", holidayDTO);

		try {
			holidayService.addHoliday(holidayDTO);
		} catch (IllegalStateException ie) {
			return ResponseEntity.badRequest().body(ie.getMessage());
		}
		return new ResponseEntity<>("Holiday entry added successfully", HttpStatus.OK);
	}

	/**
	 * To fetch all holiday entries from db
	 * @return Holiday entries in List
	 */
	@GetMapping("/getAll")
	public List<HolidayDTO> getAll() {
		return holidayService.getAll();
	}

	/**
	 * To update Holiday entry by Country code and Holiday date and the update values pass it Request body
	 * @param countryCode country code should be ISO3 country code
	 * @param holidayDate Accept date in ISO format and it limits to current year
	 * @param updateRequest DTO object to receive the Request body input
	 * @return update result
	 */
	@PutMapping("/updateHolidayByCodeAndDate")
	public ResponseEntity<String> updateHolidayByCodeAndDate(@RequestParam("country_code") String countryCode,
			@RequestParam("holiday_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate holidayDate,
			@RequestBody HolidayUpdateRequest updateRequest) {

		if (!Misc.isISO3CountryCode(countryCode)) {
			return ResponseEntity.badRequest().body("Country code should be valid ISO3 Country code.");
		}

		if (!Misc.isDateInCurrentYear(holidayDate)) {
			return ResponseEntity.badRequest().body("Holiday date should be current year.");
		}

		if (updateRequest.getHoliday_date() == null || !StringUtils.hasText(updateRequest.getHoliday_name())) {
			return ResponseEntity.badRequest().body("Request Body Holiday Date/Name should not be empty.");
		}

		if (!Misc.isDateInCurrentYear(updateRequest.getHoliday_date())) {
			return ResponseEntity.badRequest().body("Request Body Holiday date should be current year.");
		}

		log.info("updateHolidayByCodeAndDate() - countryCode: {} holidayDate: {} HolidayUpdateRequest: {} ",
				countryCode, holidayDate, updateRequest);

		HolidayResponse holiday = holidayService.updateHolidayByCodeAndDate(countryCode, holidayDate, updateRequest);
		return new ResponseEntity<>("Holiday entry updated successfully" + System.lineSeparator() + holiday,
				HttpStatus.OK);
	}

	/**
	 * To update Holiday entry by Country code and Holiday name
	 * @param countryCode country code should be ISO3 country code
	 * @param holidayName Holiday name
	 * @param updateRequest DTO object to receive the Request body input
	 * @return End point status
	 */
	@PutMapping("/updateHolidayByCodeAndName")
	public ResponseEntity<String> updateHolidayByCodeAndName(@RequestParam("country_code") String countryCode,
			@RequestParam("holiday_name") String holidayName, @RequestBody HolidayUpdateRequest updateRequest) {

		if (!Misc.isISO3CountryCode(countryCode)) {
			return ResponseEntity.badRequest().body("Country code should be valid ISO3 Country code.");
		}

		if (updateRequest.getHoliday_date() == null || !StringUtils.hasText(updateRequest.getHoliday_name())) {
			return ResponseEntity.badRequest().body("Request Body Holiday Date/Name should not be empty.");
		}

		if (!Misc.isDateInCurrentYear(updateRequest.getHoliday_date())) {
			return ResponseEntity.badRequest().body("Request Body Holiday date should be current year.");
		}

		HolidayResponse holiday = holidayService.updateHolidayByCodeAndName(countryCode, holidayName, updateRequest);
		return new ResponseEntity<>("Holiday entry updated successfully" + System.lineSeparator() + holiday,
				HttpStatus.OK);
	}

	/**
	 * To search by country code
	 * @param countryCode country code should be ISO3 country code
	 * @return return search result
	 */
	@GetMapping("/searchByCountryCode")
	public List<HolidayDTO> searchByCountryCode(@RequestParam("country_code") String countryCode) {

		if (!Misc.isISO3CountryCode(countryCode)) {
			throw new MandatoryFieldException("Country code should be valid ISO3 Country code.");
		}

		return holidayService.searchByCountryCode(countryCode);
	}

	@GetMapping("/searchByCodeAndName")
	public List<HolidayDTO> searchByCountry(@RequestParam("country_code") String countryCode,
			@RequestParam("holiday_name") String holidayName) {

		if (!Misc.isISO3CountryCode(countryCode)) {
			throw new MandatoryFieldException("Country code should be valid ISO3 Country code.");
		}

		return holidayService.searchByCountryCodeAndHolidayName(countryCode, holidayName);
	}
	
	/**
	 * To delete the holiday entries for given country code
	 * @param countryCode country code should be ISO3 country code
	 * @return return status
	 */
	@DeleteMapping("/delete/{country_code}")
	public ResponseEntity<String> deleteHolidaysByCountryCode(@PathVariable("country_code") String countryCode) {
	
		if (!StringUtils.hasText(countryCode)) {
			throw new MandatoryFieldException("Country code is mandatory for delete holidays.");
		} else if (!Misc.isISO3CountryCode(countryCode)) {
			throw new MandatoryFieldException("Country code should be valid ISO3 Country code.");
		}		
		
		holidayService.deleteHolidaysByCountryCode(countryCode);
		return ResponseEntity.ok("Holidays deleted for the given country code(" + countryCode + ")");
	}

	/**
	 * To delete the holiday by using country code and holiday date
	 * @param countryCode country code should be ISO3 country code
	 * @param holidayDate Accept date in ISO format and it limits to current year
	 * @return delete status
	 */
	@DeleteMapping("/delete/{country_code}/{holiday_date}")
	public ResponseEntity<String> deleteByCountryCodeAndHolidayDate(@PathVariable("country_code") String countryCode,
			@PathVariable("holiday_date") String holidayDate) {
		
		if (!StringUtils.hasText(countryCode)) {
			throw new MandatoryFieldException("Country code is mandatory for delete holidays.");
		} else if (!Misc.isISO3CountryCode(countryCode)) {
			throw new MandatoryFieldException("Country code should be valid ISO3 Country code.");
		}
		
		if (!StringUtils.hasText(holidayDate)) {
			throw new MandatoryFieldException("Holiday Date is mandatory for delete holidays.");
		} else if (!Misc.isValidDate(holidayDate)) {
			throw new MandatoryFieldException("Holiday Date is should be ISO format YYYY-MM-dd.");
		}
	
		LocalDate hDate = Misc.parseDate(holidayDate);
	
		long deletedCount = holidayService.deleteByCountryCodeAndDate(countryCode, hDate);
		return ResponseEntity.ok(deletedCount + " holiday(s) deleted for the given country code(" + countryCode + ") and holiday date(" + hDate + ")");
	}

	/**
	 * To upload multiple CSV file(s) to load data initially
	 * @param files CSV file template to accept file
	 * @return number row(s) inserted and rejected details
	 */
	@Operation(summary = "Upload file(s)", description = "Uploads a file using multipart/form-data")
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadHolidays(
			@Parameter(description = "File to upload", required = true) @RequestPart("files") List<MultipartFile> files) {
		int fileNo = 1;
		StringBuilder response = new StringBuilder();
		String tmp;
		try {
			for (MultipartFile file : files) {

				if (file == null || file.isEmpty()) {
					throw new IllegalArgumentException("Uploaded file is empty.");
				}
				response.append("File ").append(fileNo).append(" : ").append(file.getOriginalFilename()).append(System.lineSeparator());
				tmp = holidayService.parseAndSaveCsv(file);
				response.append(tmp);
				fileNo++;
			}
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading files.");
			}
		return ResponseEntity.ok(response.toString());
	}
}