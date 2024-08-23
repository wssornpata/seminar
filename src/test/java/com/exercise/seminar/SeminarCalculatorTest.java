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
    private String filePath;

    @BeforeEach
    void setUp() {
        dataQueue = new LinkedBlockingQueue<>();
        filePath = "src/main/java/com/exercise/seminar/file/input.txt";
        seminarCalculator = new SeminarCalculator(dataQueue ,new QueueProducer(filePath, dataQueue));
    }

    @Test
    void testIsNineAM() {
        LocalDateTime nineAM = LocalDateTime.of(2023, 10, 10, 9, 0);
        LocalDateTime notNineAM = LocalDateTime.of(2023, 10, 10, 10, 0);

        assertTrue(seminarCalculator.isNineAM(nineAM));
        assertFalse(seminarCalculator.isNineAM(notNineAM));
    }

    @Test
    void testIsMatchPattern() {
        String line = "Sit Down and Write 30min";
        String wrongLine = "Sit Down and Write";

        assertTrue(seminarCalculator.isMatchPattern(line));
        assertFalse(seminarCalculator.isMatchPattern(wrongLine));
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
    void testIsMoreFivePM() {
        LocalDateTime afterFivePM = LocalDateTime.of(2023, 10, 10, 17, 5);
        LocalDateTime beforeFivePM = LocalDateTime.of(2023, 10, 10, 11, 0);

        assertTrue(seminarCalculator.isAfterFivePM(afterFivePM));
        assertFalse(seminarCalculator.isAfterFivePM(beforeFivePM));
    }


    @Test
    void testIsLastIndexAndEndDay() throws InterruptedException {
        LocalDateTime networkingEvent = LocalDateTime.of(2023, 10, 10, 16, 0);
        LocalDateTime notNetworkingEvent = LocalDateTime.of(2023, 10, 10, 13, 0);

        //size = 1
        dataQueue.put( "Pair Programming vs Noise 45min");
        assertFalse(seminarCalculator.isLastIndexAndEndDay(networkingEvent));
        assertFalse(seminarCalculator.isLastIndexAndEndDay(notNetworkingEvent));
        //size = 0
        dataQueue.take();
        assertFalse(seminarCalculator.isLastIndexAndEndDay(notNetworkingEvent));
        assertTrue(seminarCalculator.isLastIndexAndEndDay(networkingEvent));
    }

    @Test
    void testFormatToThaiBuddhistDate() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 10, 10, 9, 0);
        String expectedDate = "10/10/2566";

        assertEquals(expectedDate, SeminarCalculator.changeThaiBuddhistFormat(dateTime));
    }

}