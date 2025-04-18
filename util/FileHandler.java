package util;

import model.Expense;
import model.User;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FileHandler {

    public static void saveExpenseToFile(Expense expense, String filePath) {
        StringBuilder sb = new StringBuilder();

        sb.append("{\n");
        sb.append("  \"description\": \"").append(expense.getDescription()).append("\",\n");
        sb.append("  \"payer\": \"").append(expense.getPayer().getName()).append("\",\n");
        sb.append("  \"amount\": ").append(expense.getAmount()).append(",\n");

        sb.append("  \"participants\": [\n");
        List<User> participants = expense.getParticipants();
        for (int i = 0; i < participants.size(); i++) {
            sb.append("    \"").append(participants.get(i).getName()).append("\"");
            if (i < participants.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ],\n");

        sb.append("  \"splitDetails\": {\n");
        int count = 0;
        for (Map.Entry<User, Double> entry : expense.getSplitDetails().entrySet()) {
            sb.append("    \"").append(entry.getKey().getName()).append("\": ").append(entry.getValue());
            if (++count < expense.getSplitDetails().size()) sb.append(",");
            sb.append("\n");
        }
        sb.append("  }\n");

        sb.append("},\n");

        try (FileWriter fw = new FileWriter(filePath, true)) {
            fw.write(sb.toString());
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
    public static void clearFile(String filePath) {
        try (FileWriter fw = new FileWriter(filePath, false)) {
            fw.write(""); // Overwrite with nothing
        } catch (IOException e) {
            System.out.println("Error clearing file: " + e.getMessage());
        }
    }
    
}
