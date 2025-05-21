package com.holiday.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableTransactionManagement
@OpenAPIDefinition(
		info = @Info(
				title = "Holiday System",
				version = "1.0",
				description = "API documentation for Holiday System"
		)
)

public class HolidaySystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(HolidaySystemApplication.class, args);
	}

}
