package com.exercise.seminar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

class SeminarCalculatorTest {

    private SeminarCalculator seminarCalculator;
    private BlockingQueue<String> dataQueue;

    @BeforeEach
    void setUp() {
        dataQueue = new LinkedBlockingQueue<>();
        seminarCalculator = new SeminarCalculator(dataQueue);
    }

    @Test
    void testIsNineAM() {
        LocalDateTime nineAM = LocalDateTime.of(2023, 10, 10, 9, 0);
        LocalDateTime notNineAM = LocalDateTime.of(2023, 10, 10, 10, 0);

        assertTrue(seminarCalculator.isNineAM(nineAM));
        assertFalse(seminarCalculator.isNineAM(notNineAM));
    }

    @Test
    void testIsLunch() {
        LocalDateTime lunchTime = LocalDateTime.of(2023, 10, 10, 12, 0);
        LocalDateTime notLunchTime = LocalDateTime.of(2023, 10, 10, 11, 0);

        assertTrue(seminarCalculator.isLunch(lunchTime));
        assertFalse(seminarCalculator.isLunch(notLunchTime));
    }

    @Test
    void testIsNetworkingEvent() {
        LocalDateTime networkingTime = LocalDateTime.of(2023, 10, 10, 17, 0);
        LocalDateTime notNetworkingTime = LocalDateTime.of(2023, 10, 10, 16, 0);

        assertTrue(seminarCalculator.isNetworkingEvent(networkingTime));
        assertFalse(seminarCalculator.isNetworkingEvent(notNetworkingTime));
    }

    @Test
    void testFormatToThaiBuddhistDate() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 10, 10, 9, 0);
        String expectedDate = "10/10/2566";

        assertEquals(expectedDate, SeminarCalculator.chageToBuddhaFormat(dateTime));
    }
}