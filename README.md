# Expense Splitter App

A JavaFX-based desktop application that allows users to track and split group expenses efficiently.

## Features

- Add users to a group
- Add expenses with:
  - Payer
  - Amount
  - Description
  - Selected participants
- Split expenses equally among participants
- Track who owes whom and how much
- View full expense history
- Simple, responsive GUI with dark mode
- Data is saved locally to `data/expenses.json`

## Technologies Used

- Java 17+
- JavaFX 23.0.2
- File-based persistence (JSON)
- CSS for styling


## How to Run

### 1. Compile

Make sure JavaFX is downloaded and update the path below:

```bash
javac --module-path "C:\javafx-sdk-23.0.2\lib" --add-modules javafx.controls -cp . app/*.java model/*.java service/*.java util/*.java
java --module-path "C:\javafx-sdk-23.0.2\lib" --add-modules javafx.controls -cp . app.ExpenseSplitterApp

