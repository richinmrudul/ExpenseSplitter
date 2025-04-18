package util;

import model.Expense;
import model.User;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileHandler {

    public static void clearFile(String filePath) {
        try (FileWriter fw = new FileWriter(filePath, false)) {
            fw.write("");
        } catch (IOException e) {
            System.out.println("Error clearing file: " + e.getMessage());
        }
    }

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

    // 👉 NEW METHOD: Save users to JSON
    public static void saveUsers(List<User> users, String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("[");
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                pw.printf("  { \"id\": \"%s\", \"name\": \"%s\" }", u.getId(), u.getName());
                if (i < users.size() - 1) pw.println(",");
                else pw.println();
            }
            pw.println("]");
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    // 👉 NEW METHOD: Load users from JSON
    public static List<User> loadUsers(String filePath) {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String id = "", name = "";
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("\"id\"")) {
                    id = line.split(":")[1].trim().replace("\"", "").replace(",", "");
                } else if (line.startsWith("\"name\"")) {
                    name = line.split(":")[1].trim().replace("\"", "").replace(",", "");
                    users.add(new User(id, name));
                }
            }
        } catch (IOException e) {
            // ignore if file doesn't exist
        }
        return users;
    }
}
