package com.holiday.system.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

public class Misc {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
	public static Set<String> iso3CountryCodes = new HashSet<>();

	static {
		for (String iso2Country : Locale.getISOCountries()) {
			Locale locale = new Locale("", iso2Country);
			try {
				iso3CountryCodes.add(locale.getISO3Country().toUpperCase());
			} catch (MissingResourceException e) {
				// Skip locales without ISO3 country codes
			}
		}
	}

	public static LocalDate isValidDateFormat(String holidayDateStr) {
		LocalDate holidayDate = null;
		try {
			return LocalDate.parse(holidayDateStr);
		} catch (DateTimeParseException e) {
			return holidayDate;
		}
	}

	public static boolean isISO3CountryCode(String code) {
		return code != null && iso3CountryCodes.contains(code.toUpperCase());
	}

	public static boolean isDateInCurrentYear(LocalDate dt) {
		if (dt == null || dt.getYear() != LocalDate.now().getYear()) {
			return false;
		} else {
			return true;
		}
	}
	
	public static LocalDate parseDate(String dateStr) {
		try {
			return LocalDate.parse(dateStr, FORMATTER);
		} catch (DateTimeParseException e) {
			System.out.println("Invalid date format: " + dateStr);
			return null;
		}
	}
	
	public static boolean isValidDate(String dateStr) {
		return parseDate(dateStr) != null;
	}
}
