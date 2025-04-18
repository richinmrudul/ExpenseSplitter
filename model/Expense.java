package model;

import java.util.List;
import java.util.Map;

public class Expense {
    private String description;
    private User payer;
    private double amount;
    private List<User> participants;
    private Map<User, Double> splitDetails;

    public Expense(String description, User payer, double amount, List<User> participants, Map<User, Double> splitDetails) {
        this.description = description;
        this.payer = payer;
        this.amount = amount;
        this.participants = participants;
        this.splitDetails = splitDetails;
    }

    // getter and other helpers
    public String getDescription() { return description; }
    public User getPayer() { return payer; }
    public double getAmount() { return amount; }
    public List<User> getParticipants() { return participants; }
    public Map<User, Double> getSplitDetails() { return splitDetails; }
}
