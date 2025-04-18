package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.User;
import service.ExpenseService;

import java.util.*;

public class ExpenseSplitterApp extends Application {

    private List<User> users = new ArrayList<>();
    private ExpenseService expenseService = new ExpenseService();
    private VBox balanceBox = new VBox(5);
    private ComboBox<String> payerDropdown;
    private VBox participantCheckboxes = new VBox(5);
    private ListView<String> historyList = new ListView<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("💸 Expense Splitter");

        TabPane tabPane = new TabPane();

        // ===== USERS TAB =====
        VBox usersTabRoot = new VBox(15);
        usersTabRoot.setPadding(new Insets(20));

        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter user name");
        Button addUserBtn = new Button("Add User");

        ListView<String> userListView = new ListView<>();

        addUserBtn.setOnAction(e -> {
            String name = userNameField.getText().trim();
            if (!name.isEmpty()) {
                User newUser = new User("u" + (users.size() + 1), name);
                users.add(newUser);
                userNameField.clear();
                userListView.getItems().add(newUser.getName());
                payerDropdown.getItems().add(newUser.getName());

                CheckBox cb = new CheckBox(newUser.getName());
                participantCheckboxes.getChildren().add(cb);

                updateBalances();
            }
        });

        usersTabRoot.getChildren().addAll(
                new Label("👤 Add a User:"),
                new HBox(10, userNameField, addUserBtn),
                new Label("👥 Current Users:"),
                userListView
        );

        Tab usersTab = new Tab("👥 Users", usersTabRoot);
        usersTab.setClosable(false);

        // ===== BALANCES TAB =====
        VBox balancesTabRoot = new VBox(15);
        balancesTabRoot.setPadding(new Insets(20));

        payerDropdown = new ComboBox<>();
        payerDropdown.setPromptText("Select payer");

        TextField amountField = new TextField();
        TextField descField = new TextField();
        amountField.setPromptText("Amount");
        descField.setPromptText("Description");

        Button addExpenseBtn = new Button("Add Equal Split Expense");

        participantCheckboxes.setPadding(new Insets(5));
        VBox participantBoxWrapper = new VBox(5,
                new Label("Select participants:"),
                new ScrollPane(participantCheckboxes));
        participantBoxWrapper.setPrefHeight(150);

        addExpenseBtn.setOnAction(e -> {
            String payerName = payerDropdown.getValue();
            if (payerName == null || payerName.isBlank()) {
                showAlert("Please select a payer.");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountField.getText().trim());
            } catch (Exception ex) {
                showAlert("Invalid amount.");
                return;
            }

            User payer = users.stream()
                    .filter(u -> u.getName().equalsIgnoreCase(payerName))
                    .findFirst().orElse(null);

            if (payer == null) {
                showAlert("Payer not found.");
                return;
            }

            List<User> selectedParticipants = new ArrayList<>();
            for (javafx.scene.Node node : participantCheckboxes.getChildren()) {
                if (node instanceof CheckBox cb && cb.isSelected()) {
                    users.stream()
                            .filter(u -> u.getName().equals(cb.getText()))
                            .findFirst()
                            .ifPresent(selectedParticipants::add);
                }
            }

            if (selectedParticipants.isEmpty()) {
                showAlert("Please select at least one participant.");
                return;
            }

            if (!selectedParticipants.contains(payer)) {
                selectedParticipants.add(payer);
            }

            String description = descField.getText().trim();
            expenseService.addExpense(payer, amount, selectedParticipants, description);
            addToHistory(payer, amount, description, selectedParticipants);

            payerDropdown.getSelectionModel().clearSelection();
            amountField.clear();
            descField.clear();
            participantCheckboxes.getChildren().forEach(node -> {
                if (node instanceof CheckBox cb) cb.setSelected(false);
            });

            updateBalances();
        });

        Label balancesLabel = new Label("Current Balances:");
        balanceBox.setPadding(new Insets(10));
        updateBalances();

        VBox expenseInputSection = new VBox(10,
                new HBox(10, payerDropdown, amountField, descField, addExpenseBtn),
                participantBoxWrapper
        );

        balancesTabRoot.getChildren().addAll(expenseInputSection, balancesLabel, balanceBox);
        Tab balancesTab = new Tab("💳 Balances", balancesTabRoot);
        balancesTab.setClosable(false);

        // ===== HISTORY TAB =====
        VBox historyTabRoot = new VBox(10);
        historyTabRoot.setPadding(new Insets(20));
        historyList.setPrefHeight(300);
        historyTabRoot.getChildren().addAll(
                new Label("🧾 Expense History:"),
                historyList
        );

        Tab historyTab = new Tab("🧾 History", historyTabRoot);
        historyTab.setClosable(false);

        // SCENE & STYLING
        tabPane.getTabs().addAll(usersTab, balancesTab, historyTab);
        Scene scene = new Scene(tabPane, 700, 550);
        scene.getStylesheets().add("styles/style.css"); // 🔥 DARK MODE CSS
        stage.setScene(scene);
        stage.show();
    }

    private void addToHistory(User payer, double amount, String description, List<User> participants) {
        StringBuilder sb = new StringBuilder();
        sb.append(payer.getName())
                .append(" paid $")
                .append(String.format("%.2f", amount))
                .append(" for ")
                .append(description)
                .append(" split with: ");

        List<String> names = participants.stream()
                .filter(u -> !u.equals(payer))
                .map(User::getName)
                .toList();

        sb.append(String.join(", ", names));
        historyList.getItems().add(sb.toString());
    }

    private void updateBalances() {
        balanceBox.getChildren().clear();
        Map<User, Map<User, Double>> balances = expenseService.getBalances();

        for (User u1 : balances.keySet()) {
            for (Map.Entry<User, Double> entry : balances.get(u1).entrySet()) {
                if (entry.getValue() > 0) {
                    Label lbl = new Label(u1.getName() + " owes " + entry.getKey().getName() + ": $" + String.format("%.2f", entry.getValue()));
                    balanceBox.getChildren().add(lbl);
                }
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
    }
}
