package com.exercise.seminar;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//@SpringBootApplication
public class SeminarApplication {

	public static void main(String[] args) {
//		SpringApplication.run(SeminarApplication.class, args);
		Scanner sc = new Scanner(System.in);
		String questionInput = "Enter filename: ";
		String filePath;
		SeminarCalculator seminarCalculator;

		BlockingQueue<String> dataQueue = new LinkedBlockingQueue<>();
		while (true) {
			System.out.print(questionInput);
			filePath = sc.nextLine();
			 filePath = "src/main/java/com/exercise/seminar/file/input.txt";
			try {
				QueueProducer queueProducer = new QueueProducer(filePath, dataQueue);
				queueProducer.start();
				seminarCalculator = new SeminarCalculator(dataQueue);
				seminarCalculator.calculateSeminar();
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
}
