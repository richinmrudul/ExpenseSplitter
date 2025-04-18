package app;

import model.User;
import model.Group;
import service.ExpenseService;
import util.FileHandler;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FileHandler.clearFile("data/expenses.json");

        List<User> allUsers = FileHandler.loadUsers("data/users.json");
        Map<String, User> userMap = allUsers.stream()
            .collect(Collectors.toMap(u -> u.getName().toLowerCase(), u -> u));

        Group group = new Group("Default Group");
        ExpenseService expenseService = new ExpenseService();

        System.out.println("💸 Welcome to Expense Splitter CLI");

        while (true) {
            System.out.println("\n0. Add User");
            System.out.println("1. Add Expense");
            System.out.println("2. Show Balances");
            System.out.println("3. Settle Up");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice;

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("⚠️ Invalid input. Enter a number.");
                continue;
            }

            switch (choice) {
                case 0 -> {
                    System.out.print("Enter new user name: ");
                    String name = scanner.nextLine().trim();
                    String key = name.toLowerCase();
                    if (userMap.containsKey(key)) {
                        System.out.println("❗ User already exists.");
                        break;
                    }
                    String id = "u" + (allUsers.size() + 1);
                    User user = new User(id, name);
                    allUsers.add(user);
                    userMap.put(key, user);
                    group.addUser(user);
                    System.out.println("✅ User added: " + name);
                    FileHandler.saveUsers(allUsers, "data/users.json");
                }

                case 1 -> {
                    if (allUsers.isEmpty()) {
                        System.out.println("❌ No users found. Add users first.");
                        break;
                    }

                    System.out.print("Enter payer name: ");
                    String payerName = scanner.nextLine().toLowerCase();
                    User payer = userMap.get(payerName);
                    if (payer == null) {
                        System.out.println("❌ Payer not found.");
                        break;
                    }

                    System.out.print("Enter amount: ");
                    double amount;
                    try {
                        amount = Double.parseDouble(scanner.nextLine());
                    } catch (Exception e) {
                        System.out.println("❌ Invalid amount.");
                        break;
                    }

                    System.out.print("Enter description: ");
                    String description = scanner.nextLine();

                    System.out.print("Enter participants (comma-separated): ");
                    String[] names = scanner.nextLine().toLowerCase().split(",");
                    List<User> participants = new ArrayList<>();
                    for (String name : names) {
                        User participant = userMap.get(name.trim());
                        if (participant != null) participants.add(participant);
                    }

                    if (participants.isEmpty()) {
                        System.out.println("❌ No valid participants found.");
                        break;
                    }

                    System.out.println("Split type:");
                    System.out.println("1. Equally");
                    System.out.println("2. By Exact Amount");
                    System.out.println("3. By Percentage");
                    System.out.print("Choose split type: ");
                    int splitType = Integer.parseInt(scanner.nextLine());
                    
                    switch (splitType) {
                        case 1 -> expenseService.addExpense(payer, amount, participants, description);
                    
                        case 2 -> {
                            Map<User, Double> exactSplits = new HashMap<>();
                            double total = 0.0;
                    
                            for (User user : participants) {
                                System.out.print("Enter amount for " + user.getName() + ": ");
                                double amt = Double.parseDouble(scanner.nextLine());
                                exactSplits.put(user, amt);
                                total += amt;
                            }
                    
                            if (Math.abs(total - amount) > 0.01) {
                                System.out.println("❌ Error: Entered amounts do not match total.");
                            } else {
                                expenseService.addCustomExpense(payer, amount, participants, description, exactSplits);
                            }
                        }
                    
                        case 3 -> {
                            Map<User, Double> percentSplits = new HashMap<>();
                            double totalPercent = 0.0;
                    
                            for (User user : participants) {
                                System.out.print("Enter percentage for " + user.getName() + ": ");
                                double pct = Double.parseDouble(scanner.nextLine());
                                percentSplits.put(user, pct);
                                totalPercent += pct;
                            }
                    
                            if (Math.abs(totalPercent - 100.0) > 0.01) {
                                System.out.println("❌ Error: Percentages must add up to 100%.");
                            } else {
                                Map<User, Double> computedSplits = new HashMap<>();
                                for (User user : percentSplits.keySet()) {
                                    double share = (percentSplits.get(user) / 100.0) * amount;
                                    computedSplits.put(user, Math.round(share * 100.0) / 100.0);
                                }
                    
                                expenseService.addCustomExpense(payer, amount, participants, description, computedSplits);
                            }
                        }
                    
                        default -> System.out.println("❌ Invalid split type.");
                    }
                    
                }

                case 2 -> {
                    if (allUsers.isEmpty()) {
                        System.out.println("❌ No users available.");
                        break;
                    }
                    expenseService.showBalances();
                }

                case 3 -> {
                    if (allUsers.isEmpty()) {
                        System.out.println("❌ No users available.");
                        break;
                    }

                    System.out.print("Who is paying? ");
                    String payerName = scanner.nextLine().toLowerCase();
                    User payer = userMap.get(payerName);
                    if (payer == null) {
                        System.out.println("❌ Invalid payer.");
                        break;
                    }

                    System.out.print("Who is receiving? ");
                    String payeeName = scanner.nextLine().toLowerCase();
                    User payee = userMap.get(payeeName);
                    if (payee == null) {
                        System.out.println("❌ Invalid payee.");
                        break;
                    }

                    System.out.print("Enter amount: ");
                    double amount;
                    try {
                        amount = Double.parseDouble(scanner.nextLine());
                    } catch (Exception e) {
                        System.out.println("❌ Invalid amount.");
                        break;
                    }

                    expenseService.settleUp(payer, payee, amount);
                }

                case 4 -> {
                    System.out.println("👋 Exiting. Goodbye!");
                    scanner.close();
                    return;
                }

                default -> System.out.println("❌ Invalid option. Try again.");
            }
        }
    }
}
