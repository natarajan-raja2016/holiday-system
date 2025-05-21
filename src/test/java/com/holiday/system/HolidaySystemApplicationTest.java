package com.holiday.system;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HolidaySystemApplicationTest {

    @Test
    void testMainMethod() {
        // Act & Assert
        assertDoesNotThrow(() -> HolidaySystemApplication.main(new String[]{}));
    }
}
