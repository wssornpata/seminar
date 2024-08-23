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
    private final QueueProducer queueProducer;
    private final DateTimeFormatter dateTimeformatter;
    private final DateTimeFormatter timeFormatter;
    private final Pattern minutePattern;
    private Matcher minuteMatcher;

    public SeminarCalculator(BlockingQueue<String> dataQueue, QueueProducer queueProducer) {
        this.dataQueue = dataQueue;
        this.queueProducer = queueProducer;
        this.fileManager = new FileManager();
        this.minutePattern = Pattern.compile(minRegex);
        this.dateTimeformatter = DateTimeFormatter.ofPattern(datePattern);
        this.timeFormatter =DateTimeFormatter.ofPattern(timePattern);
    }

    public String calculateSeminar() {
        StringBuilder timeline = new StringBuilder();
        int countDay = 1;
        try {
            Thread.sleep(100);

            String date = dataQueue.poll();
            LocalDateTime seminarDateTime = LocalDate.parse(date, dateTimeformatter).atTime(9, 0);

            String line;
            while (!dataQueue.isEmpty()) {
                line = dataQueue.take();
                if (isMatchPattern(line) && !line.isEmpty()) {
//                    timeline.setLength(0);

                    if(isNineAM(seminarDateTime)){
                        appendDay(timeline, countDay, seminarDateTime);
                        countDay++;
                    }

                    int minute = Integer.parseInt(minuteMatcher.group(1));
                    LocalDateTime newDateTime = seminarDateTime.plusMinutes(minute);

                    if (isLunch(newDateTime)) {
                        appendSeminarDetail(timeline, seminarDateTime, line);
                        appendLunch(timeline);
                        setToAfternoon(seminarDateTime);
                    } else if (isNetworkingEvent(newDateTime)) {
                        if ((newDateTime.getHour() >= 17 && newDateTime.getMinute() > 0)) {
                            appendNetworkingEvent(timeline, seminarDateTime);
                        } else {
                            appendSeminarDetail(timeline, seminarDateTime, line);
                            appendNetworkingEvent(timeline, newDateTime);
                        }
                        setToNextMorning(seminarDateTime);
                    } else {
                        appendSeminarDetail(timeline, seminarDateTime, line);
                        if(isLastIndexAndEndDay(newDateTime)){
                            appendNetworkingEvent(timeline, newDateTime);
                        }
                        seminarDateTime = newDateTime;
                    }

//                    fileManager.appendToSeminarFile(timeline.toString());

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
        }
        return timeline.toString();
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

    private void appendDay(StringBuilder timeline, int countDay, LocalDateTime seminarDateTime) {
        timeline.append("\n").append("Day ").append(countDay).append(" - ").append(changeThaiBuddhistFormat(seminarDateTime)).append("\n");
    }

    private void appendSeminarDetail(StringBuilder timeline, LocalDateTime seminarDateTime, String line) {
        timeline.append(getTime(seminarDateTime)).append(" ").append(line).append("\n");
    }

    private void appendLunch(StringBuilder timeline) {
        LocalTime lunchTime = LocalTime.of(12, 0);
        timeline.append(getTime(lunchTime)).append(" Lunch").append("\n");
    }

    private void appendNetworkingEvent(StringBuilder timeline, LocalDateTime dateTime) {
        timeline.append(getTime(dateTime)).append(" Networking Event").append("\n");
    }

    public boolean isMatchPattern(String line) {
        minuteMatcher = minutePattern.matcher(line);
        return  minuteMatcher.find();
    }

    public boolean isLunch(LocalDateTime newDateTime) {
        return (newDateTime.getHour() >= 12 && newDateTime.getHour() < 13);
    }

    public void setToAfternoon(LocalDateTime seminarDateTime) {
        seminarDateTime.withHour(13).withMinute(0);
    }

    public void setToNextMorning(LocalDateTime seminarDateTime) {
        seminarDateTime.plusDays(1).withHour(9).withMinute(0);
    }

    public boolean isNetworkingEvent(LocalDateTime newDateTime) {
        return (newDateTime.getHour() > 16);
    }

    public boolean isLastIndexAndEndDay(LocalDateTime newDateTime) {
        return (dataQueue.size() == 0 && newDateTime.getHour() >= 16);
    }

    public static String changeThaiBuddhistFormat(LocalDateTime dateTime) {
        ThaiBuddhistDate thaiBuddhistDate = ThaiBuddhistDate.from(dateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return formatter.format(thaiBuddhistDate);
    }
}