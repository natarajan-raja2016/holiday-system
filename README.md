# Holiday System

A Spring Boot web application for managing holidays.

## Project Structure

- **Main Class:** `com.holiday.system.HolidaySystemApplication`
- **Build Tool:** Maven
- **Java Version:** 21+ (compatible with Spring Boot 3+)

---

## Getting Started

### Prerequisites

Ensure the following tools are installed:

- Java 21 or higher
- Maven 3.8+ (or use the included Maven Wrapper: `mvnw`)
- Git


### Access the Application
Once the application is running, you can interact with the Rest API using swagger-ui:

http://localhost:8080/swagger-ui/index.html#

### API Endpoints:

POST /api/holidays/upload – Add Holidays by upload one or more file(s) containing the country and its holidays in CSV file

#### Sample File Format:
country_code, country_name, holiday_date, holiday_name
USD, United States, 2025-011-01, New Year
USD, United States, 2025-01-20, Martin Luther King day

Sample URL:
http://localhost:8080/api/holidays/upload

POST /api/holidays/add – Add a Holiday by pass holiday information in request body

Sample URL:
http://localhost:8080/api/holidays/add

GET /api/holidays/getAll – Retrieve all countries and its holidays

GET /api/holidays/searchByCountryCode?country_code=<Country Code> – Retrieve all holidays related to the given country code

GET /api/holidays/searchByCodeAndName?country_code=<Country Code>&holiday_name=<Holiday Name> – Retrieve a holiday details based on given country code and holiday name

PUT /api/holidays/updateHolidayByCodeAndName?country_code=<Country Code>&holiday_name=<Holiday Name> – Update Holiday by country code and holiday name

PUT /api/holidays/updateHolidayByCodeAndDate?country_code=<Country Code>&holiday_date=<Holiday Date> – Update Holiday by country code and holiday date

DELETE /api/holidays/delete/{country_code} – Delete the holidays based on the country code passed in the path parameter.

DELETE /api/holidays/delete/{country_code}/{holiday_date} – Delete the holidays based on the country code and holidate passed in the path parameter.