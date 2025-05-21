package com.holiday.system.controller;

import com.holiday.system.dto.HolidayDTO;
import com.holiday.system.dto.HolidayResponse;
import com.holiday.system.dto.HolidayUpdateRequest;
import com.holiday.system.exception.MandatoryFieldException;
import com.holiday.system.model.Country;
import com.holiday.system.service.HolidayService;
import com.holiday.system.util.Misc;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class HolidayControllerTest {

    @InjectMocks
    private HolidayController holidayController;

    @Mock
    private HolidayService holidayService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddHoliday_Success() throws Exception {
        HolidayDTO dto = new HolidayDTO();
        dto.setHoliday_date(LocalDate.of(2025, 1, 1));
        dto.setCountry_code("USA");
        dto.setCountry_name("America");
        dto.setHoliday_name("New Year");

        ResponseEntity<String> response = holidayController.addHoliday(dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("successfully"));
        verify(holidayService).addHoliday(dto);
    }

    @Test
    void testAddHoliday_InvalidDate() throws Exception {
        HolidayDTO dto = new HolidayDTO();
        dto.setCountry_code("USA");
        dto.setCountry_name("America");
        dto.setHoliday_name("New Year");

        dto.setHoliday_date(LocalDate.of(2024, 1, 1));

        ResponseEntity<String> response = holidayController.addHoliday(dto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("current year"));
    }

    @Test
    void testUpdateHolidayByCodeAndDate_Success() throws Exception {
        HolidayUpdateRequest req = new HolidayUpdateRequest();
        req.setHoliday_date(LocalDate.now());
        req.setHoliday_name("Test Day");
        Country country = new Country();

        HolidayResponse holidayResponse = new HolidayResponse("USA", LocalDate.now(), "Mon", country);
		when(holidayService.updateHolidayByCodeAndDate(any(), any(), any()))
                .thenReturn(holidayResponse);

        ResponseEntity<String> response = holidayController.updateHolidayByCodeAndDate("USA", LocalDate.now(), req);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateHolidayByCodeAndName_ValidationFail() throws BadRequestException {
    	HolidayUpdateRequest req = new HolidayUpdateRequest();
        req.setHoliday_date(LocalDate.now());
        req.setHoliday_name(null);

        ResponseEntity<String> response = holidayController.updateHolidayByCodeAndName("USA", "New Year", req);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    
    @Test
    void deleteHolidaysByCountryCode_validInput() {
        try (MockedStatic<Misc> miscMock = Mockito.mockStatic(Misc.class)) {
            miscMock.when(() -> Misc.isISO3CountryCode("USA")).thenReturn(true);

            var response = holidayController.deleteHolidaysByCountryCode("USA");

            verify(holidayService).deleteHolidaysByCountryCode("USA");
            assertEquals(200, response.getStatusCodeValue());
            assertTrue(response.getBody().contains("Holidays deleted for the given country code(USA)"));
        }
    }

    @Test
    void deleteHolidaysByCountryCode_emptyCountryCode_shouldThrow() {
        MandatoryFieldException ex = assertThrows(MandatoryFieldException.class, () ->
                holidayController.deleteHolidaysByCountryCode("   ")
        );
        assertEquals("Country code is mandatory for delete holidays.", ex.getMessage());
    }

    @Test
    void deleteHolidaysByCountryCode_invalidCountryCode_shouldThrow() {
        try (MockedStatic<Misc> miscMock = Mockito.mockStatic(Misc.class)) {
            miscMock.when(() -> Misc.isISO3CountryCode("XYZ")).thenReturn(false);

            MandatoryFieldException ex = assertThrows(MandatoryFieldException.class, () ->
                    holidayController.deleteHolidaysByCountryCode("XYZ")
            );
            assertEquals("Country code should be valid ISO3 Country code.", ex.getMessage());
        }
    }

    // ------------------------
    // deleteByCountryCodeAndHolidayDate
    // ------------------------

    @Test
    void deleteByCountryCodeAndHolidayDate_validInput() {
        String countryCode = "USA";
        String holidayDate = "2025-01-01";
        LocalDate parsedDate = LocalDate.of(2025, 1, 1);

        try (MockedStatic<Misc> miscMock = Mockito.mockStatic(Misc.class)) {
            miscMock.when(() -> Misc.isISO3CountryCode(countryCode)).thenReturn(true);
            miscMock.when(() -> Misc.isValidDate(holidayDate)).thenReturn(true);
            miscMock.when(() -> Misc.parseDate(holidayDate)).thenReturn(parsedDate);

            var response = holidayController.deleteByCountryCodeAndHolidayDate(countryCode, holidayDate);

            verify(holidayService).deleteByCountryCodeAndDate(countryCode, parsedDate);
            assertEquals(200, response.getStatusCodeValue());
            assertTrue(response.getBody().contains("holiday(s) deleted for the given country code(USA) and holiday date(2025-01-01)"));
        }
    }

    @Test
    void deleteByCountryCodeAndHolidayDate_emptyCountryCode_shouldThrow() {
        MandatoryFieldException ex = assertThrows(MandatoryFieldException.class, () ->
                holidayController.deleteByCountryCodeAndHolidayDate("   ", "2025-01-01")
        );
        assertEquals("Country code is mandatory for delete holidays.", ex.getMessage());
    }

    @Test
    void deleteByCountryCodeAndHolidayDate_invalidCountryCode_shouldThrow() {
        try (MockedStatic<Misc> miscMock = Mockito.mockStatic(Misc.class)) {
            miscMock.when(() -> Misc.isISO3CountryCode("XYZ")).thenReturn(false);

            MandatoryFieldException ex = assertThrows(MandatoryFieldException.class, () ->
                    holidayController.deleteByCountryCodeAndHolidayDate("XYZ", "2025-01-01")
            );
            assertEquals("Country code should be valid ISO3 Country code.", ex.getMessage());
        }
    }

    @Test
    void deleteByCountryCodeAndHolidayDate_emptyDate_shouldThrow() {
        try (MockedStatic<Misc> miscMock = Mockito.mockStatic(Misc.class)) {
            miscMock.when(() -> Misc.isISO3CountryCode("USA")).thenReturn(true);

            MandatoryFieldException ex = assertThrows(MandatoryFieldException.class, () ->
                    holidayController.deleteByCountryCodeAndHolidayDate("USA", "   ")
            );
            assertEquals("Holiday Date is mandatory for delete holidays.", ex.getMessage());
        }
    }

    @Test
    void deleteByCountryCodeAndHolidayDate_invalidDate_shouldThrow() {
        try (MockedStatic<Misc> miscMock = Mockito.mockStatic(Misc.class)) {
            miscMock.when(() -> Misc.isISO3CountryCode("USA")).thenReturn(true);
            miscMock.when(() -> Misc.isValidDate("bad-date")).thenReturn(false);

            MandatoryFieldException ex = assertThrows(MandatoryFieldException.class, () ->
                    holidayController.deleteByCountryCodeAndHolidayDate("USA", "bad-date")
            );
            assertEquals("Holiday Date is should be ISO format YYYY-MM-dd.", ex.getMessage());
        }
    }
    

    @Test
    void testUploadHolidays_SingleValidFile() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "files", "holidays.csv", "text/csv",
                "country_code,country_name,holiday_date,holiday_name\nUSA,United States,2025-12-25,Christmas"
                        .getBytes(StandardCharsets.UTF_8));

        when(holidayService.parseAndSaveCsv(any(MultipartFile.class))).thenReturn("Processed 1 record");

        MockMultipartFile file1 = new MockMultipartFile("files", "file1.csv", "text/csv", "header,data".getBytes());
        // When
        ResponseEntity<String> response = holidayController.uploadHolidays(Arrays.asList(file1));

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("File 1"));
        assertTrue(response.getBody().contains("Processed 1 record"));
    }

    @Test
    void testUploadHolidays_MultipleFiles() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("files", "file1.csv", "text/csv", "header,data".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "file2.csv", "text/csv", "header,data".getBytes());

        when(holidayService.parseAndSaveCsv(any())).thenReturn("Processed file");

        ResponseEntity<String> response = holidayController.uploadHolidays(Arrays.asList(file1, file2));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("File 1 : file1.csv"));
        assertTrue(response.getBody().contains("File 2 : file2.csv"));
    }

    @Test
    void testUploadHolidays_EmptyFile_ThrowsIllegalArgumentException() {
        MockMultipartFile emptyFile = new MockMultipartFile("files", "empty.csv", "text/csv", new byte[0]);

        ResponseEntity<String> response = holidayController.uploadHolidays(Arrays.asList(emptyFile));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error uploading files.", response.getBody());
    }

    @Test
    void testUploadHolidays_ExceptionThrownInService() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "holidays.csv", "text/csv", "some,data".getBytes());

        when(holidayService.parseAndSaveCsv(any())).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<String> response = holidayController.uploadHolidays(Arrays.asList(file));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error uploading files.", response.getBody());
    }

    @Test
    void testGetAll_ReturnsList() {
        when(holidayService.getAll()).thenReturn(Collections.singletonList(new HolidayDTO()));

        List<HolidayDTO> result = holidayController.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void testSearchByCountryCode_Success() {
        when(holidayService.searchByCountryCode("USA")).thenReturn(Collections.singletonList(new HolidayDTO()));

        List<HolidayDTO> result = holidayController.searchByCountryCode("USA");
        assertEquals(1, result.size());
    }

    @Test
    void testSearchByCountryCode_ThrowsException() {
        assertThrows(MandatoryFieldException.class, () -> {
            holidayController.searchByCountryCode("");
        });
    }

    @Test
    void testSearchByCodeAndName_Success() {
        when(holidayService.searchByCountryCodeAndHolidayName("USA", "Christmas"))
                .thenReturn(Collections.singletonList(new HolidayDTO()));

        List<HolidayDTO> result = holidayController.searchByCountry("USA", "Christmas");
        assertEquals(1, result.size());
    }

    @Test
    void testSearchByCodeAndName_ThrowsException() {
        assertThrows(MandatoryFieldException.class, () -> {
            holidayController.searchByCountry("", "");
        });
    }
    
    @Test
    void testAddHoliday_InvalidHolidayDate() throws Exception {
        HolidayDTO dtoNullDate = new HolidayDTO();
        dtoNullDate.setCountry_code("USA");
        dtoNullDate.setCountry_name("America");
        dtoNullDate.setHoliday_name("New Year");
        dtoNullDate.setHoliday_date(null);



        ResponseEntity<String> responseNullDate = holidayController.addHoliday(dtoNullDate);
        assertEquals(HttpStatus.BAD_REQUEST, responseNullDate.getStatusCode());
        assertEquals("Holiday date should be current year.", responseNullDate.getBody());

        HolidayDTO dtoInvalidYear = new HolidayDTO();
        dtoInvalidYear.setCountry_code("USA");
        dtoInvalidYear.setCountry_name("America");
        dtoInvalidYear.setHoliday_name("New Year");
        dtoInvalidYear.setHoliday_date(LocalDate.of(2020, 12, 25));

        ResponseEntity<String> responseInvalidYear = holidayController.addHoliday(dtoInvalidYear);
        assertEquals(HttpStatus.BAD_REQUEST, responseInvalidYear.getStatusCode());
        assertEquals("Holiday date should be current year.", responseInvalidYear.getBody());
    }
    
    @Test
    void testUpdateHolidayByCodeAndDate_ValidRequest() {
        // Given
        HolidayUpdateRequest validRequest = new HolidayUpdateRequest();
        validRequest.setHoliday_name("Christmas");
        validRequest.setHoliday_date(LocalDate.now());

        HolidayResponse mockResponse = new HolidayResponse("New Year", LocalDate.now(), "Mon", new Country()); // could be enhanced
        when(holidayService.updateHolidayByCodeAndDate(any(), any(), any()))
                .thenReturn(mockResponse);

        try (MockedStatic<Misc> miscMock = mockStatic(Misc.class)) {
            miscMock.when(() -> Misc.isISO3CountryCode("USA")).thenReturn(true);
            miscMock.when(() -> Misc.isDateInCurrentYear(LocalDate.now())).thenReturn(true);

            ResponseEntity<String> response = holidayController.updateHolidayByCodeAndDate("USA", LocalDate.now(), validRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().contains("Holiday entry updated successfully"));
        }
    }

    @Test
    void testUpdateHolidayByCodeAndDate_InvalidCountryCode() {
        HolidayUpdateRequest request = new HolidayUpdateRequest();
        request.setHoliday_name("Christmas");
        request.setHoliday_date(LocalDate.now());

        try (MockedStatic<Misc> miscMock = mockStatic(Misc.class)) {
            miscMock.when(() -> Misc.isISO3CountryCode("INVALID")).thenReturn(false);

            ResponseEntity<String> response = holidayController.updateHolidayByCodeAndDate("INVALID", LocalDate.now(), request);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Country code should be valid ISO3 Country code.", response.getBody());
        }
    }

    @Test
    void testUpdateHolidayByCodeAndDate_InvalidHolidayDate() {
        HolidayUpdateRequest request = new HolidayUpdateRequest();
        request.setHoliday_name("Christmas");
        request.setHoliday_date(LocalDate.now());

        try (MockedStatic<Misc> miscMock = mockStatic(Misc.class)) {
            miscMock.when(() -> Misc.isISO3CountryCode("USA")).thenReturn(true);
            miscMock.when(() -> Misc.isDateInCurrentYear(LocalDate.of(2020, 12, 25))).thenReturn(false);

            ResponseEntity<String> response = holidayController.updateHolidayByCodeAndDate("USA", LocalDate.of(2020, 12, 25), request);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Holiday date should be current year.", response.getBody());
        }
    }

    @Test
    void testUpdateHolidayByCodeAndDate_EmptyRequestBodyFields() {
        HolidayUpdateRequest request = new HolidayUpdateRequest(); // name is null, date is null

        try (MockedStatic<Misc> miscMock = mockStatic(Misc.class)) {
            miscMock.when(() -> Misc.isISO3CountryCode("USA")).thenReturn(true);
            miscMock.when(() -> Misc.isDateInCurrentYear(LocalDate.now())).thenReturn(true);

            ResponseEntity<String> response = holidayController.updateHolidayByCodeAndDate("USA", LocalDate.now(), request);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Request Body Holiday Date/Name should not be empty.", response.getBody());
        }
    }

    @Test
    void testUpdateHolidayByCodeAndDate_RequestBodyInvalidDate() {
        HolidayUpdateRequest request = new HolidayUpdateRequest();
        request.setHoliday_name("Christmas");
        request.setHoliday_date(LocalDate.of(2020, 12, 25)); // not current year

        try (MockedStatic<Misc> miscMock = mockStatic(Misc.class)) {
            miscMock.when(() -> Misc.isISO3CountryCode("USA")).thenReturn(true);
            miscMock.when(() -> Misc.isDateInCurrentYear(LocalDate.now())).thenReturn(true);
            miscMock.when(() -> Misc.isDateInCurrentYear(LocalDate.of(2020, 12, 25))).thenReturn(false);

            ResponseEntity<String> response = holidayController.updateHolidayByCodeAndDate("USA", LocalDate.now(), request);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Request Body Holiday date should be current year.", response.getBody());
        }
    }
    
    
    @Test
    void testUpdateHolidayByCodeAndName_ValidHolidayDate() throws Exception {
        HolidayUpdateRequest validRequest = new HolidayUpdateRequest();
        validRequest.setHoliday_name("Christmas");
        validRequest.setHoliday_date(LocalDate.now());
        
        Country country = new Country();

        when(holidayService.updateHolidayByCodeAndName(anyString(), anyString(), any(HolidayUpdateRequest.class)))
                .thenReturn(new HolidayResponse("Christmas", LocalDate.now(), "USA", country));

        ResponseEntity<String> response = holidayController.updateHolidayByCodeAndName("USA", "Christmas", validRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue( response.getBody().contains("Holiday entry updated successfully"));

        verify(holidayService, times(1)).updateHolidayByCodeAndName(eq("USA"), eq("Christmas"), eq(validRequest));
    }

    @Test
    void testUpdateHolidayByCodeAndName_InvalidHolidayDateYear() throws Exception {
        HolidayUpdateRequest invalidYearRequest = new HolidayUpdateRequest();
        invalidYearRequest.setHoliday_name("New Year");
        invalidYearRequest.setHoliday_date(LocalDate.of(2024, 12, 31));

        ResponseEntity<String> response = holidayController.updateHolidayByCodeAndName("USA", "New Year", invalidYearRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Request Body Holiday date should be current year.", response.getBody());

        verify(holidayService, times(0)).updateHolidayByCodeAndName(anyString(), anyString(), any(HolidayUpdateRequest.class));
    }
    
    @Test
    void testAddHoliday_MissingCountryCode() throws Exception {
        HolidayDTO dto = new HolidayDTO();
        dto.setHoliday_date(LocalDate.now());
        dto.setCountry_name("America");
        dto.setHoliday_name("New Year");
        dto.setCountry_code("");

        ResponseEntity<String> response = holidayController.addHoliday(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Country code should be valid ISO3 Country code.", response.getBody());
    }

    @Test
    void testUpdateHolidayByCodeAndName_MissingHolidayName() throws Exception {
        HolidayUpdateRequest req = new HolidayUpdateRequest();
        req.setHoliday_date(LocalDate.now());
        req.setHoliday_name("");

        ResponseEntity<String> response = holidayController.updateHolidayByCodeAndName("US", "Holiday", req);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Country code should be valid ISO3 Country code.", response.getBody());
    }

    @Test
    void testUpdateHolidayByCodeAndName_MissingHolidayDate() throws Exception {
        HolidayUpdateRequest req = new HolidayUpdateRequest();
        req.setHoliday_name("Christmas");
        req.setHoliday_date(null);

        ResponseEntity<String> response = holidayController.updateHolidayByCodeAndName("USA", "Holiday", req);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Request Body Holiday Date/Name should not be empty.", response.getBody());
    }

    
}
