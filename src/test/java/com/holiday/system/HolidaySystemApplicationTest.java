package com.holiday.system;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HolidaySystemApplicationTest {

    @Test
    void testMainMethod() {
        assertDoesNotThrow(() -> HolidaySystemApplication.main(new String[]{}));
    }
}
