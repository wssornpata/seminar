package com.exercise.seminar;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ThaiBuddhistDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeminarCalculator {
    private final BlockingQueue<String> dataQueue;

    private final String minRegex = "(\\d+)min";
    private final String datePattern = "yyyy-MM-dd";
    private final String timePattern = "hh:mma";

    private final FileManager fileManager;
    private final DateTimeFormatter dateTimeformatter;
    private final DateTimeFormatter timeFormatter;
    private final Pattern minutePattern;
    private Matcher minuteMatcher;

    public SeminarCalculator(BlockingQueue<String> dataQueue) {
        this.dataQueue = dataQueue;
        this.fileManager = new FileManager();
        this.minutePattern = Pattern.compile(minRegex);
        this.dateTimeformatter = DateTimeFormatter.ofPattern(datePattern);
        this.timeFormatter =DateTimeFormatter.ofPattern(timePattern);
    }

    public void calculateSeminar() {
        StringBuilder timeline = new StringBuilder();
        int countDay = 1;
        try {
            Thread.sleep(500);

            String date = dataQueue.poll();
            LocalDateTime seminarDateTime = LocalDate.parse(date, dateTimeformatter).atTime(9, 0);

            String line;
            while ((line = dataQueue.poll()) != null) {
                if (isMatch(line)) {
                    timeline.setLength(0);

                    if(isNineAM(seminarDateTime)){
                        timeline.append("Day ").append(countDay).append(" - ").append(chageToBuddhaFormat(seminarDateTime)).append("\n");
                        countDay++;
                    }

                    int minute = Integer.parseInt(minuteMatcher.group(1));

                    timeline.append(getTime(seminarDateTime)).append(" ").append(line);

                    LocalDateTime newDateTime = seminarDateTime.plusMinutes(minute);

                    if (isLunch(newDateTime)) {
                        LocalTime lunchTime = LocalTime.of(12, 0);
                        timeline.append("\n").append(getTime(lunchTime)).append(" Lunch");
                        seminarDateTime = seminarDateTime.withHour(13).withMinute(0);
                    } else {
                        seminarDateTime = newDateTime;
                    }

                    if (isNetworkingEvent(newDateTime)) {
                        timeline.append("\n").append(getTime(newDateTime)).append(" Networking Event");
                        seminarDateTime = seminarDateTime.plusDays(1).withHour(9).withMinute(0);
                    }

                    fileManager.appendToSeminarFile(timeline.toString());

                    while (seminarDateTime.getDayOfWeek() == DayOfWeek.SATURDAY || seminarDateTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        seminarDateTime = seminarDateTime.plusDays(1);
                    }
                }
                Thread.sleep(30);
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    boolean isNineAM(LocalDateTime time) {
        return time.getHour() == 9 && time.getMinute() == 0;
    }

    private String getTime(LocalDateTime time) {
        return time.format(timeFormatter);
    }

    private String getTime(LocalTime time) {
        return time.format(timeFormatter);
    }

    private boolean isMatch(String line) {
        minuteMatcher = minutePattern.matcher(line);
        return  minuteMatcher.find();
    }

    boolean isLunch(LocalDateTime newDateTime) {
        return (newDateTime.getHour() >= 12 && newDateTime.getHour() < 13);
    }

    boolean isNetworkingEvent(LocalDateTime newDateTime) {
        return (newDateTime.getHour() >= 17);
    }

    public static String chageToBuddhaFormat(LocalDateTime dateTime) {
        ThaiBuddhistDate thaiBuddhistDate = ThaiBuddhistDate.from(dateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return formatter.format(thaiBuddhistDate);
    }

}
