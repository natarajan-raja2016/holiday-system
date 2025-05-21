package com.holiday.system.service;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
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

public class HolidayServiceTest {

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private HolidayMapper holidayMapper;

    @InjectMocks
    private HolidayService holidayService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddHoliday() throws Exception {
        HolidayDTO holidayDTO = new HolidayDTO();
        holidayDTO.setCountry_code("USA");
        holidayDTO.setHoliday_date(LocalDate.of(2025, 12, 25));
        holidayDTO.setHoliday_name("Christmas");
        holidayDTO.setCountry_name("United States");

        Country country = new Country();
        country.setCountryCode("USA");
        country.setCountryName("United States");

        Holiday holiday = new Holiday();
        holiday.setHolidayName("Christmas");
        holiday.setHolidayDate(LocalDate.of(2025, 12, 25));
        
        when(countryRepository.findByCountryCode("USA")).thenReturn(Optional.of(country));
        when(holidayRepository.findByCountry_CountryCodeAndHolidayDate("USA", LocalDate.of(2025, 1, 1))).thenReturn(Optional.empty());
        when(holidayMapper.toEntity(holidayDTO)).thenReturn(holiday);
        when(holidayRepository.save(holiday)).thenReturn(holiday);

        Holiday result = holidayService.addHoliday(holidayDTO);

        assertNotNull(result);
        assertEquals("Christmas", result.getHolidayName());
        verify(holidayRepository, times(1)).save(holiday);
    }

    @Test
    void testUpdateHolidayByCodeAndDate() {
        HolidayUpdateRequest updateRequest = new HolidayUpdateRequest();
        updateRequest.setHoliday_name("New Year's Eve");
        updateRequest.setHoliday_date(LocalDate.of(2025, 1, 1));
        
        Country country = new Country("USA", "United States Of America");

        Holiday holiday = new Holiday();
        holiday.setHolidayName("New Year");
        holiday.setHolidayDate(LocalDate.of(2025, 01, 01));
        holiday.setCountry(country);

        when(holidayRepository.findByCountry_CountryCodeAndHolidayDate("USA", LocalDate.of(2025, 1, 1)))
                .thenReturn(Optional.of(holiday));
        when(holidayRepository.save(holiday)).thenReturn(holiday);

        HolidayResponse response = holidayService.updateHolidayByCodeAndDate("USA", LocalDate.of(2025, 1, 1), updateRequest);

        assertNotNull(response);
        assertEquals("New Year's Eve", response.getName());
        verify(holidayRepository, times(1)).save(holiday);
    }

    @Test
    void testUpdateHolidayByCodeAndName() {
        HolidayUpdateRequest updateRequest = new HolidayUpdateRequest();
        updateRequest.setHoliday_name("Labor Day");
        updateRequest.setHoliday_date(LocalDate.of(2025, 9, 1));
        
        Country country = new Country("USA", "United States Of America");

        Holiday holiday = new Holiday();
        holiday.setHolidayName("Labor Day");
        holiday.setHolidayDate(LocalDate.of(2025, 9, 1));
        holiday.setCountry(country);

        when(holidayRepository.findByCountry_CountryCodeAndHolidayNameIgnoreCase("USA", "Labor Day"))
                .thenReturn(Optional.of(holiday));
        when(holidayRepository.save(holiday)).thenReturn(holiday);

        HolidayResponse response = holidayService.updateHolidayByCodeAndName("USA", "Labor Day", updateRequest);

        assertNotNull(response);
        assertEquals("Labor Day", response.getName());
        verify(holidayRepository, times(1)).save(holiday);
    }

    @Test
    void testGetAll() {
    	
    	Country country = new Country("USA", "United States Of America");
    	
        Holiday holiday = new Holiday();
        holiday.setHolidayName("Christmas");
        holiday.setHolidayDate(LocalDate.of(2025, 12, 25));
        holiday.setCountry(country);
        
        List<Holiday> holidayList = Arrays.asList(holiday);
        when(holidayRepository.findAll()).thenReturn(holidayList);

        List<HolidayDTO> holidayDTOList = holidayService.getAll();

        assertEquals(1, holidayDTOList.size());
        assertEquals("Christmas", holidayDTOList.get(0).getHoliday_name());
    }

    
    @Test
    void testDeleteHolidaysByCountryCode() {
        String countryCode = "USA";
        when(countryRepository.deleteByCountryCode(countryCode)).thenReturn(1);

        holidayService.deleteHolidaysByCountryCode(countryCode);

        verify(countryRepository, times(1)).deleteByCountryCode(countryCode);
    }


    @Test
    void testIsValidDateFormat() {
        String validDate = "2023-12-25";
        String invalidDate = "invalid-date";

        LocalDate validParsedDate = Misc.isValidDateFormat(validDate);
        LocalDate invalidParsedDate = Misc.isValidDateFormat(invalidDate);

        assertNotNull(validParsedDate);
        assertNull(invalidParsedDate);
    }
    
    @Test
    void testSearchByCountryCode() {
        Country country = new Country();
        country.setCountryCode("USA");
        country.setCountryName("United States");

        Holiday holiday = new Holiday();
        holiday.setHolidayDate(LocalDate.of(2025, 1, 1));
        holiday.setHolidayName("New Year");
        holiday.setCountry(country);

        when(holidayRepository.findByCountryCode("USA")).thenReturn(List.of(holiday));

        List<HolidayDTO> result = holidayService.searchByCountryCode("USA");

        assertEquals(1, result.size());
        assertEquals("USA", result.get(0).getCountry_code());
        assertEquals("United States", result.get(0).getCountry_name());
        assertEquals("New Year", result.get(0).getHoliday_name());
        assertEquals(LocalDate.of(2025, 1, 1), result.get(0).getHoliday_date());
    }

    @Test
    void testSearchByCountryCodeAndHolidayName() {
        Country country = new Country();
        country.setCountryCode("USA");
        country.setCountryName("United States");

        Holiday holiday = new Holiday();
        holiday.setHolidayDate(LocalDate.of(2025, 12, 25));
        holiday.setHolidayName("Christmas");
        holiday.setCountry(country);

        when(holidayRepository.findByCountryCodeAndHolidayName("USA", "Christmas")).thenReturn(List.of(holiday));

        List<HolidayDTO> result = holidayService.searchByCountryCodeAndHolidayName("USA", "Christmas");

        assertEquals(1, result.size());
        assertEquals("USA", result.get(0).getCountry_code());
        assertEquals("United States", result.get(0).getCountry_name());
        assertEquals("Christmas", result.get(0).getHoliday_name());
        assertEquals(LocalDate.of(2025, 12, 25), result.get(0).getHoliday_date());
    }
    
    @Test
    void testAddHoliday_ThrowsException_WhenHolidayAlreadyExists() {
        HolidayDTO dto = new HolidayDTO();
        dto.setCountry_code("USA");
        dto.setCountry_name("United States");
        dto.setHoliday_date(LocalDate.of(2025, 12, 25));
        dto.setHoliday_name("Christmas");

        Country country = new Country();
        country.setCountryCode("USA");

        Holiday existingHoliday = new Holiday();
        existingHoliday.setHolidayDate(dto.getHoliday_date());
        existingHoliday.setHolidayName(dto.getHoliday_name());
        existingHoliday.setCountry(country);

        when(holidayRepository.findByCountry_CountryCodeAndHolidayDate("USA", LocalDate.of(2025, 12, 25)))
                .thenReturn(Optional.of(existingHoliday));

        assertThrows(IllegalStateException.class, () -> holidayService.addHoliday(dto));
    }
    
    @Test
    void testUpdateHolidayByCodeAndDate_ThrowsWhenHolidayNotFound() {
        String countryCode = "USA";
        LocalDate holidayDate = LocalDate.of(2025, 12, 25);
        HolidayUpdateRequest request = new HolidayUpdateRequest();
        request.setHoliday_date(LocalDate.of(2025, 12, 31));
        request.setHoliday_name("New Year's Eve");

        when(holidayRepository.findByCountry_CountryCodeAndHolidayDate(countryCode, holidayDate))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> holidayService.updateHolidayByCodeAndDate(countryCode, holidayDate, request));
    }
    
    @Test
    void testUpdateHolidayByCodeAndDate_UpdatesDateAndName() {
        String countryCode = "USA";
        LocalDate existingDate = LocalDate.of(2025, 12, 25);

        Country country = new Country();
        country.setCountryCode(countryCode);
        country.setCountryName("United States");

        Holiday holiday = new Holiday();
        holiday.setHolidayDate(existingDate);
        holiday.setHolidayName("Christmas");
        holiday.setCountry(country);

        HolidayUpdateRequest request = new HolidayUpdateRequest();
        request.setHoliday_date(LocalDate.of(2025, 12, 31));
        request.setHoliday_name("New Year's Eve");

        when(holidayRepository.findByCountry_CountryCodeAndHolidayDate(countryCode, existingDate))
            .thenReturn(Optional.of(holiday));
        when(holidayRepository.save(holiday)).thenReturn(holiday);

        HolidayResponse response = holidayService.updateHolidayByCodeAndDate(countryCode, existingDate, request);

        assertEquals("New Year's Eve", response.getName());
        assertEquals(LocalDate.of(2025, 12, 31), response.getDate());
        assertEquals("WEDNESDAY", response.getDow()); // Depends on actual day-of-week
        assertEquals("USA", response.getCountry().getCountryCode());
    }

    @Test
    void testDeleteByCountryCodeAndDate_shouldCallRepository() {
        String countryCode = "USA";
        LocalDate date = LocalDate.of(2025, 12, 25);
        when(holidayRepository.deleteByCountryCodeAndHolidayDate(countryCode, date)).thenReturn(1);
        holidayService.deleteByCountryCodeAndDate(countryCode, date);
        verify(holidayRepository, times(1)).deleteByCountryCodeAndHolidayDate(countryCode, date);
    }

    @Test
    void testDeleteByCountryCodeAndDate_noRecordsFound() {
        String countryCode = "IND";
        LocalDate date = LocalDate.of(2025, 8, 15);
        when(holidayRepository.deleteByCountryCodeAndHolidayDate(countryCode, date)).thenReturn(0);
        holidayService.deleteByCountryCodeAndDate(countryCode, date);
        verify(holidayRepository).deleteByCountryCodeAndHolidayDate(countryCode, date);
    }
    
    
    @Test
    void testValidCsv_shouldInsertHoliday() throws Exception {
        String csv = createCSV("USA,United States,2025-12-25,Christmas");
        MultipartFile file = getMockFile(csv);

        when(holidayRepository.findByCountry_CountryCodeAndHolidayDate("USA", LocalDate.parse("2025-12-25")))
                .thenReturn(Optional.empty());

        when(countryRepository.findByCountryCode("USA")).thenReturn(Optional.of(new Country()));
        when(holidayMapper.toEntity(any())).thenReturn(new Holiday());

        when(holidayRepository.save(any())).thenReturn(new Holiday());

        String output = holidayService.parseAndSaveCsv(file);
        assertTrue(output.contains("Number of successfully records : 1"));
        assertTrue(output.contains("Number of rejected records : 0"));
    }

    @Test
    void testInvalidHeaders_shouldThrowException() {
        String csv = "wrong1,wrong2,wrong3,wrong4\nvalue1,value2,value3,value4";
        MultipartFile file = getMockFile(csv);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> holidayService.parseAndSaveCsv(file));
        assertTrue(ex.getMessage().contains("Invalid CSV headers"));
    }

    @Test
    void testEmptyFields_shouldReject() throws Exception {
        String csv = createCSV(",,2025-12-25,Christmas");
        MultipartFile file = getMockFile(csv);

        String result = holidayService.parseAndSaveCsv(file);
        assertTrue(result.contains("mandatory field(s) are empty so ignored"));
    }

    @Test
    void testInvalidDate_shouldReject() throws Exception {
        String csv = createCSV("USA,United States,invalid-date,Christmas");
        MultipartFile file = getMockFile(csv);

        String result = holidayService.parseAndSaveCsv(file);
        assertTrue(result.contains("Invalid Date format so ignored"));
    }

    @Test
    void testNonCurrentYearDate_shouldReject() throws Exception {
        String csv = createCSV("USA,United States,2023-01-01,New Year");
        MultipartFile file = getMockFile(csv);

        try (MockedStatic<Misc> miscMock = Mockito.mockStatic(Misc.class)) {
            miscMock.when(() -> Misc.isValidDateFormat("2023-01-01")).thenReturn(LocalDate.of(2023, 1, 1));
            miscMock.when(() -> Misc.isDateInCurrentYear(any())).thenReturn(false);

            String result = holidayService.parseAndSaveCsv(file);
            assertTrue(result.contains("Holiday date should be current year so ignored"));
        }
    }

    @Test
    void testInvalidCountryCode_shouldReject() throws Exception {
        String csv = createCSV("XX,Unknown,2025-01-01,FakeHoliday");
        MultipartFile file = getMockFile(csv);

        try (MockedStatic<Misc> miscMock = Mockito.mockStatic(Misc.class)) {
            miscMock.when(() -> Misc.isValidDateFormat("2025-01-01")).thenReturn(LocalDate.of(2025, 1, 1));
            miscMock.when(() -> Misc.isDateInCurrentYear(any())).thenReturn(true);
            miscMock.when(() -> Misc.isISO3CountryCode("XX")).thenReturn(false);

            String result = holidayService.parseAndSaveCsv(file);
            assertTrue(result.contains("Country code should be ISO3 Country code so ignored"));
        }
    }

    @Test
    void testParseAndSaveCsv_WhenAddHolidayThrowsIllegalStateException_ShouldRejectRecord() throws Exception {
        String csvContent = "country_code,country_name,holiday_date,holiday_name\n" +
                            "USA,United States,2025-12-25,Christmas";
        MockMultipartFile file = new MockMultipartFile("files", "holidays.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        try (MockedStatic<Misc> miscMock = mockStatic(Misc.class)) {
            miscMock.when(() -> Misc.isValidDateFormat("2025-12-25")).thenReturn(LocalDate.of(2025, 12, 25));
            miscMock.when(() -> Misc.isDateInCurrentYear(any())).thenReturn(true);
            miscMock.when(() -> Misc.isISO3CountryCode("USA")).thenReturn(true);

            HolidayRepository mockHolidayRepo = mock(HolidayRepository.class);
            CountryRepository mockCountryRepo = mock(CountryRepository.class);
            HolidayMapper mockMapper = mock(HolidayMapper.class);

            HolidayService spyService = Mockito.spy(new HolidayService(mockHolidayRepo, mockCountryRepo, mockMapper));
            
            doThrow(new IllegalStateException("Holiday already exists"))
                    .when(spyService).addHoliday(any());

            String result = spyService.parseAndSaveCsv(file);
            assertTrue(result.contains("Row 2 - Holiday already exists"));
            assertTrue(result.contains("Number of successfully records : 0"));
            assertTrue(result.contains("Number of rejected records : 1"));
        }
    }


    @Test
    void testCountryNotFound_shouldCreateNewCountry() throws Exception {
        String csv = createCSV("IND,India,2025-08-15,Independence Day");
        MultipartFile file = getMockFile(csv);

        when(holidayRepository.findByCountry_CountryCodeAndHolidayDate(any(), any())).thenReturn(Optional.empty());
        when(countryRepository.findByCountryCode("IND")).thenReturn(Optional.empty());
        when(countryRepository.save(any())).thenReturn(new Country());
        when(holidayMapper.toEntity(any())).thenReturn(new Holiday());
        when(holidayRepository.save(any())).thenReturn(new Holiday());

        try (MockedStatic<Misc> miscMock = Mockito.mockStatic(Misc.class)) {
            miscMock.when(() -> Misc.isValidDateFormat("2025-08-15")).thenReturn(LocalDate.of(2025, 8, 15));
            miscMock.when(() -> Misc.isDateInCurrentYear(any())).thenReturn(true);
            miscMock.when(() -> Misc.isISO3CountryCode("IND")).thenReturn(true);

            String result = holidayService.parseAndSaveCsv(file);
            assertTrue(result.contains("Number of successfully records : 1"));
        }
    }
    
    private String createCSV(String content) {
        return "country_code,country_name,holiday_date,holiday_name\n" + content;
    }

    private MultipartFile getMockFile(String csvContent) {
        return new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));
    }

}
