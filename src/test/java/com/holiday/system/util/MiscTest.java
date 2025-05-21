package com.holiday.system.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class MiscTest {

	@Test
	void testIsValidDateFormat_WithValidDate() {
		LocalDate result = Misc.isValidDateFormat("2025-12-25");
		assertEquals(LocalDate.of(2025, 12, 25), result);
	}

	@Test
	void testIsValidDateFormat_WithInvalidDate() {
		LocalDate result = Misc.isValidDateFormat("25-12-2025");
		assertNull(result);
	}

	@Test
	void testIsISO3CountryCode_WithValidCode() {
		String iso3 = new Locale("", "US").getISO3Country();
		assertTrue(Misc.isISO3CountryCode(iso3));
	}

	@Test
	void testIsISO3CountryCode_WithLowercaseValidCode() {
		assertTrue(Misc.isISO3CountryCode("usa"));
	}

	@Test
	void testIsISO3CountryCode_WithInvalidCode() {
		assertFalse(Misc.isISO3CountryCode("XXX"));
	}

	@Test
	void testIsISO3CountryCode_WithNullCode() {
		assertFalse(Misc.isISO3CountryCode(null));
	}

	@Test
	void testIsDateInCurrentYear_WithCurrentYearDate() {
		LocalDate now = LocalDate.now();
		assertTrue(Misc.isDateInCurrentYear(now));
	}

	@Test
	void testIsDateInCurrentYear_WithDifferentYearDate() {
		LocalDate past = LocalDate.of(1999, 1, 1);
		assertFalse(Misc.isDateInCurrentYear(past));
	}

	@Test
	void testIsDateInCurrentYear_WithNull() {
		assertFalse(Misc.isDateInCurrentYear(null));
	}

	@Test
	void testParseDate_WithValidFormat() {
		LocalDate result = Misc.parseDate("2025-01-01");
		assertEquals(LocalDate.of(2025, 1, 1), result);
	}

	@Test
	void testParseDate_WithInvalidFormat() {
		LocalDate result = Misc.parseDate("01/01/2025");
		assertNull(result);
	}

	@Test
	void testISO3CountryCodeInitialization_NotEmpty() {
		assertFalse(Misc.iso3CountryCodes.isEmpty());
		assertTrue(Misc.iso3CountryCodes.contains("USA"));
	}

	@Test
	public void testValidDate() {
		assertTrue(Misc.isValidDate("2024-12-25"));
	}

	@Test
	public void testInvalidDate() {
		assertFalse(Misc.isValidDate("invalid-date"));
	}
}
