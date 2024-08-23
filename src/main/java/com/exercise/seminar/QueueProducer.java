package com.exercise.seminar;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class QueueProducer extends Thread {
    private int maxSize = 200;
    private BlockingQueue<String> dataQueue;
    private String filePath;
    private boolean running = true;
    private boolean finished = false;

    public QueueProducer(String filePath, BlockingQueue<String> dataQueue) {
        this.filePath = filePath;
        this.dataQueue = dataQueue;
    }

    @Override
    public void run() {
        System.out.println("Producer started");
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null && running) {
                while (dataQueue.size() >= maxSize) {
                    System.out.println("Queue is full, Pause Reading");
                    Thread.sleep(500);
                }
                Thread.sleep(10);
                dataQueue.put(line);
                System.out.println("Data in queue: " + dataQueue.size());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            this.running = false;
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
            this.running = false;
        }
        finished = true;
        System.out.println("Producer finished");
    }

    public void stopProducer() {
        this.running = false;
    }

    public boolean isFinished() {
        return finished;
    }
}
