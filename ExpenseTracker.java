import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ExpenseTracker {
    private final List<Expense> expenses;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter;

    public ExpenseTracker() {
        expenses = new ArrayList<>();
        scanner = new Scanner(System.in);
        dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public void start() {
        boolean running = true;
        
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    addExpense();
                    break;
                case 2:
                    viewAllExpenses();
                    break;
                case 3:
                    viewExpensesByCategory();
                    break;
                case 4:
                    generateMonthlyReport();
                    break;
                case 5:
                    running = false;
                    System.out.println("Thank you for using Expense Tracker. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        scanner.close();
    }
    
    private void displayMenu() {
        System.out.println("\n===== EXPENSE TRACKER =====");
        System.out.println("1. Add Expense");
        System.out.println("2. View All Expenses");
        System.out.println("3. View Expenses by Category");
        System.out.println("4. Generate Monthly Report");
        System.out.println("5. Exit");
        System.out.println("==========================");
    }
    
    private void addExpense() {
        System.out.println("\n----- Add New Expense -----");
        
        String description = getStringInput("Enter description: ");
        double amount = getDoubleInput("Enter amount: ");
        String category = getStringInput("Enter category (Food, Transportation, Housing, Entertainment, Other): ");
        LocalDate date = getDateInput("Enter date (yyyy-MM-dd): ");
        
        Expense expense = new Expense(description, amount, category, date);
        expenses.add(expense);
        
        System.out.println("Expense added successfully!");
    }
    
    private void viewAllExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }
        
        System.out.println("\n----- All Expenses -----");
        System.out.printf("%-5s %-20s %-10s %-15s %-10s%n", "ID", "Description", "Amount", "Category", "Date");
        System.out.println("----------------------------------------------------------");
        
        for (int i = 0; i < expenses.size(); i++) {
            Expense expense = expenses.get(i);
            System.out.printf("%-5d %-20s $%-9.2f %-15s %-10s%n", 
                i + 1, 
                expense.getDescription(), 
                expense.getAmount(), 
                expense.getCategory(), 
                expense.getDate().format(dateFormatter));
        }
        
        System.out.printf("%nTotal: $%.2f%n", calculateTotal(expenses));
    }
    
    private void viewExpensesByCategory() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }
        
        Map<String, List<Expense>> categorizedExpenses = new HashMap<>();
        
        for (Expense expense : expenses) {
            String category = expense.getCategory();
            if (!categorizedExpenses.containsKey(category)) {
                categorizedExpenses.put(category, new ArrayList<>());
            }
            categorizedExpenses.get(category).add(expense);
        }
        
        System.out.println("\n----- Expenses by Category -----");
        
        for (Map.Entry<String, List<Expense>> entry : categorizedExpenses.entrySet()) {
            String category = entry.getKey();
            List<Expense> categoryExpenses = entry.getValue();
            double categoryTotal = calculateTotal(categoryExpenses);
            
            System.out.printf("%n%s: $%.2f%n", category, categoryTotal);
            System.out.printf("%-20s %-10s %-10s%n", "Description", "Amount", "Date");
            System.out.println("------------------------------------------");
            
            for (Expense expense : categoryExpenses) {
                System.out.printf("%-20s $%-9.2f %-10s%n", 
                    expense.getDescription(), 
                    expense.getAmount(), 
                    expense.getDate().format(dateFormatter));
            }
        }
    }
    
    private void generateMonthlyReport() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }
        
        int year = getIntInput("Enter year: ");
        int month = getIntInput("Enter month (1-12): ");
        
        if (month < 1 || month > 12) {
            System.out.println("Invalid month. Month must be between 1 and 12.");
            return;
        }
        
        List<Expense> monthlyExpenses = new ArrayList<>();
        Map<String, Double> categoryTotals = new HashMap<>();
        
        for (Expense expense : expenses) {
            LocalDate date = expense.getDate();
            if (date.getYear() == year && date.getMonthValue() == month) {
                monthlyExpenses.add(expense);
                
                String category = expense.getCategory();
                double amount = expense.getAmount();
                categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
            }
        }
        
        if (monthlyExpenses.isEmpty()) {
            System.out.printf("No expenses found for %d-%02d.%n", year, month);
            return;
        }
        
        double monthlyTotal = calculateTotal(monthlyExpenses);
        
        System.out.printf("%n----- Monthly Report: %d-%02d -----%n", year, month);
        System.out.printf("Total expenses: $%.2f%n%n", monthlyTotal);
        
        System.out.println("Expenses by Category:");
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            System.out.printf("%-15s $%-9.2f (%.1f%%)%n", 
                entry.getKey(), 
                entry.getValue(), 
                (entry.getValue() / monthlyTotal) * 100);
        }
    }
    
    private double calculateTotal(List<Expense> expenseList) {
        double total = 0;
        for (Expense expense : expenseList) {
            total += expense.getAmount();
        }
        return total;
    }
    
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value < 0) {
                    System.out.println("Amount cannot be negative. Please enter a positive number.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    private LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String dateString = scanner.nextLine().trim();
                return LocalDate.parse(dateString, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
    }
}