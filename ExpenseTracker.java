package miniProject;
import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Transaction {
    String type; // income or expense
    String category;
    double amount;
    LocalDate date;

    public Transaction(String type, String category, double amount, LocalDate date) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    @Override
    public String toString() {
        // Format date as dd-MM-yyyy when saving
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return type + "," + category + "," + amount + "," + date.format(formatter);
    }
}

public class ExpenseTracker {
    static List<Transaction> transactions = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Expense Tracker ===");
            System.out.println("1. Add Transaction");
            System.out.println("2. Load Transactions from File");
            System.out.println("3. Save Transactions to File");
            System.out.println("4. View Monthly Summary");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    addTransaction();
                    break;
                case 2:
                    loadFromFile();
                    break;
                case 3:
                    saveToFile();
                    break;
                case 4:
                    viewMonthlySummary();
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    static void addTransaction() {
        System.out.print("Is this income or expense? ");
        String type = sc.nextLine().toLowerCase();

        if (!type.equals("income") && !type.equals("expense")) {
            System.out.println("Invalid type. Must be 'income' or 'expense'.");
            return;
        }

        System.out.print("Enter category (e.g., salary/business/food/rent/travel): ");
        String category = sc.nextLine();

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(sc.nextLine());

        System.out.print("Enter date (dd-MM-yyyy): ");
        String dateString = sc.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date;
        try {
            date = LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use dd-MM-yyyy.");
            return;
        }

        transactions.add(new Transaction(type, category, amount, date));
        System.out.println("Transaction added.");
    }

    static void saveToFile() {
        System.out.print("Enter filename to save (e.g., data.csv): ");
        String filename = sc.nextLine();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write header
            writer.write("type,category,amount,date");
            writer.newLine();

            for (Transaction t : transactions) {
                writer.write(t.toString());
                writer.newLine();
            }
            System.out.println("Transactions saved to file.");
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    static void loadFromFile() {
        System.out.print("Enter filename to load (e.g., data.csv): ");
        String filename = sc.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // Skip header line

            int count = 0;
            while ((line = reader.readLine()) != null) {
                System.out.println("Reading line: " + line);
                String[] parts = line.split(",");
                if (parts.length != 4) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }
                String type = parts[0].trim();
                String category = parts[1].trim();
                double amount = Double.parseDouble(parts[2].trim());
                LocalDate date = LocalDate.parse(parts[3].trim(), formatter);
                transactions.add(new Transaction(type, category, amount, date));
                count++;
            }
            System.out.println(count + " transactions loaded.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error parsing data: " + e.getMessage());
        }
    }

    static void viewMonthlySummary() {
        System.out.print("Enter year (e.g., 2025): ");
        int year = Integer.parseInt(sc.nextLine());

        System.out.print("Enter month number (1-12): ");
        int month = Integer.parseInt(sc.nextLine());

        double incomeTotal = 0, expenseTotal = 0;
        Map<String, Double> incomeByCategory = new HashMap<>();
        Map<String, Double> expenseByCategory = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.date.getYear() == year && t.date.getMonthValue() == month) {
                if (t.type.equals("income")) {
                    incomeTotal += t.amount;
                    incomeByCategory.put(t.category, incomeByCategory.getOrDefault(t.category, 0.0) + t.amount);
                } else {
                    expenseTotal += t.amount;
                    expenseByCategory.put(t.category, expenseByCategory.getOrDefault(t.category, 0.0) + t.amount);
                }
            }
        }

        System.out.println("\n--- Monthly Summary for " + Month.of(month) + " " + year + " ---");
        System.out.println("Total Income: ₹" + incomeTotal);
        for (String cat : incomeByCategory.keySet()) {
            System.out.println("  " + cat + ": ₹" + incomeByCategory.get(cat));
        }

        System.out.println("Total Expenses: ₹" + expenseTotal);
        for (String cat : expenseByCategory.keySet()) {
            System.out.println("  " + cat + ": ₹" + expenseByCategory.get(cat));
        }

        System.out.println("Net Savings: ₹" + (incomeTotal - expenseTotal));
    }
}
