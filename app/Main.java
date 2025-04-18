package app;
import model.User;
import model.Group;
import service.ExpenseService;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        util.FileHandler.clearFile("data/expenses.json");
        User u1 = new User("u1", "Richi");
        User u2 = new User("u2", "Alex");
        User u3 = new User("u3", "Sam");
        

        Group trip = new Group("Trip to Goa");
        trip.addUser(u1);
        trip.addUser(u2);
        trip.addUser(u3);

        ExpenseService expenseService = new ExpenseService();

        // Richi pays 150 for lunch, split equally
        expenseService.addExpense(u1, 150, Arrays.asList(u1, u2, u3), "Lunch at beach shack");

        // Alex pays 90 for taxi, split equally
        expenseService.addExpense(u2, 90, Arrays.asList(u1, u2, u3), "Taxi ride");

        expenseService.showBalances();
        System.out.println("\n--- Settling Up ---");
        expenseService.settleUp(u1, u2, 30); // Richi pays Alex
        expenseService.settleUp(u3, u1, 50); // Sam pays Richi
        expenseService.showBalances(); // See updated balances

    }
}
