package com.exercise.seminar;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class FileManager {

    private String pathOutput = "src/main/java/com/exercise/seminar/file/output.txt";

    public FileManager() {
    }

    public void appendToSeminarFile(String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathOutput, true))) {
            writer.write(content);
            writer.newLine();
        }
    }

}