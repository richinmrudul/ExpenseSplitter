package service;
import util.FileHandler;

import model.Expense;
import model.User;

import java.util.*;

public class ExpenseService {
    private Map<User, Map<User, Double>> balances;

    public ExpenseService() {
        balances = new HashMap<>();
    }

    public void addExpense(User payer, double amount, List<User> participants, String description) {
        double splitAmount = amount / participants.size();
        Map<User, Double> splitMap = new HashMap<>();

        for (User participant : participants) {
            if (!participant.equals(payer)) {
                splitMap.put(participant, splitAmount);
                updateBalance(payer, participant, splitAmount);
            }
        }

        Expense expense = new Expense(description, payer, amount, participants, splitMap);
        FileHandler.saveExpenseToFile(expense, "data/expenses.json");
        System.out.println("Expense added: " + expense.getDescription());
    }

    private void updateBalance(User payer, User participant, double amount) {
        balances.putIfAbsent(participant, new HashMap<>());
        balances.putIfAbsent(payer, new HashMap<>());

        // participant owes payer
        double previous = balances.get(participant).getOrDefault(payer, 0.0);
        balances.get(participant).put(payer, previous + amount);
    }

    public void showBalances() {
        System.out.println("\n--- Current Balances ---");
        for (User user : balances.keySet()) {
            for (Map.Entry<User, Double> entry : balances.get(user).entrySet()) {
                if (entry.getValue() != 0.0) {
                    System.out.printf("%s owes %s: $%.2f%n", user.getName(), entry.getKey().getName(), entry.getValue());
                }
            }
        }
    }
    public void settleUp(User payer, User payee, double amount) {
        if (!balances.containsKey(payer) || !balances.get(payer).containsKey(payee)) {
            System.out.println("No outstanding balance to settle.");
            return;
        }
    
        double currentOwed = balances.get(payer).get(payee);
        if (amount >= currentOwed) {
            balances.get(payer).remove(payee);
            System.out.printf("%s settled their full debt to %s of $%.2f%n", payer.getName(), payee.getName(), currentOwed);
        } else {
            balances.get(payer).put(payee, currentOwed - amount);
            System.out.printf("%s paid $%.2f to %s. Remaining: $%.2f%n", payer.getName(), amount, payee.getName(), currentOwed - amount);
        }
    }
    public void addCustomExpense(User payer, double amount, List<User> participants, String description, Map<User, Double> splits) {
        Map<User, Double> splitMap = new HashMap<>();
    
        for (User participant : participants) {
            if (!participant.equals(payer)) {
                double share = splits.getOrDefault(participant, 0.0);
                splitMap.put(participant, share);
                updateBalance(payer, participant, share);
            }
        }
    
        Expense expense = new Expense(description, payer, amount, participants, splitMap);
        FileHandler.saveExpenseToFile(expense, "data/expenses.json");
        System.out.println("✅ Expense added: " + expense.getDescription());
    }
    
    public Map<User, Map<User, Double>> getBalances() {
        return balances;
    }
    
    
}
