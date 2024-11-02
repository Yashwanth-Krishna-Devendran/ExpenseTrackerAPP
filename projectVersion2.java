// Import necessary libraries for UI components, event handling, file operations, etc.
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

// Main Application Class
public class javaProjectP1 extends JFrame implements Serializable {
    private List<Expense> expenses;   // List to store all expenses
    private List<Task> tasks;         // List to store all tasks
    private JTextArea displayArea;    // Area to display information
    private JLabel summaryLabel;      // Label to show summary of expenses and tasks

    // Constructor to initialize the main UI and data loading
    public javaProjectP1() {
        expenses = loadExpenses();   // Load saved expenses from file
        tasks = loadTasks();         // Load saved tasks from file

        // Setting up the main frame properties
        setTitle("Expense Tracker & To-Do List");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tabbed pane for switching between "Expenses" and "Tasks" sections
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Expenses", createExpensePanel());  // Tab for expenses
        tabbedPane.add("Tasks", createTaskPanel());        // Tab for tasks
        add(tabbedPane);

        displaySummary();  // Display summary of current expenses and tasks

        // Save data when the window is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();   // Call to save all data
            }
        });
    }

    // Method to create the Expense panel layout and functionality
    private JPanel createExpensePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Form panel for input fields and button
        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        JTextField titleField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField categoryField = new JTextField();

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryField);

        // Button to add expense
        JButton addExpenseButton = new JButton("Add Expense");
        formPanel.add(addExpenseButton);
        panel.add(formPanel, BorderLayout.NORTH);

        // Text area to display added expenses
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        panel.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // Action for add expense button
        addExpenseButton.addActionListener(e -> {
            try {
                // Retrieve and parse input values
                String title = titleField.getText();
                String description = descriptionField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String category = categoryField.getText();

                // Create and add the new expense object
                Expense expense = new Expense(title, description, amount, category, new Date());
                expenses.add(expense);
                displayArea.append("Added Expense: " + expense.getDetails() + "\n");

                // Refresh summary and clear form fields
                displaySummary();
                titleField.setText("");
                descriptionField.setText("");
                amountField.setText("");
                categoryField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid number.");
            }
        });

        return panel;
    }

    // Method to create the Task panel layout and functionality
    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Form panel for input fields and button
        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        JTextField titleField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField priorityField = new JTextField();

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);
        formPanel.add(new JLabel("Priority (Low/Medium/High):"));
        formPanel.add(priorityField);

        // Button to add task
        JButton addTaskButton = new JButton("Add Task");
        formPanel.add(addTaskButton);
        panel.add(formPanel, BorderLayout.NORTH);

        // Text area to display added tasks
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        panel.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // Action for add task button
        addTaskButton.addActionListener(e -> {
            String title = titleField.getText();
            String description = descriptionField.getText();
            String priority = priorityField.getText();

            // Create and add the new task object
            Task task = new Task(title, description, new Date(), priority);
            tasks.add(task);
            displayArea.append("Added Task: " + task.getDetails() + "\n");

            // Clear form fields
            titleField.setText("");
            descriptionField.setText("");
            priorityField.setText("");
        });

        return panel;
    }

    // Method to calculate and display the summary of expenses and tasks
    private void displaySummary() {
        double totalAmount = expenses.stream().mapToDouble(Expense::getAmount).sum();
        long expenseCount = expenses.size();
        long taskCount = tasks.size();

        // Update or create the summary label
        if (summaryLabel == null) {
            summaryLabel = new JLabel();
            add(summaryLabel, BorderLayout.SOUTH);
        }
        summaryLabel.setText("Total Expenses: $" + totalAmount + " | Expenses: " + expenseCount + " | Tasks: " + taskCount);
    }

    // Save all expenses and tasks to files
    private void saveData() {
        new Thread(() -> {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("expenses.ser"))) {
                oos.writeObject(expenses);
                System.out.println("Expenses saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("tasks.ser"))) {
                oos.writeObject(tasks);
                System.out.println("Tasks saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Load saved expenses from file
    private List<Expense> loadExpenses() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("expenses.ser"))) {
            return (List<Expense>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No saved expenses found. Starting fresh.");
            return new ArrayList<>();
        }
    }

    // Load saved tasks from file
    private List<Task> loadTasks() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("tasks.ser"))) {
            return (List<Task>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No saved tasks found. Starting fresh.");
            return new ArrayList<>();
        }
    }

    // Class to represent an Expense with details and display methods
    static class Expense implements Serializable {
        private String title;
        private String description;
        private double amount;
        private String category;
        private Date date;

        public Expense(String title, String description, double amount, String category, Date date) {
            this.title = title;
            this.description = description;
            this.amount = amount;
            this.category = category;
            this.date = date;
        }

        public String getTitle() { return title; }
        public double getAmount() { return amount; }

        // Method to get details of the expense in string format
        public String getDetails() {
            return title + " - " + description + " - Rs. " + amount + " - " + category + " - " + date;
        }
    }

    // Class to represent a Task with details and display methods
    static class Task implements Serializable {
        private String title;
        private String description;
        private Date date;
        private String priority;

        public Task(String title, String description, Date date, String priority) {
            this.title = title;
            this.description = description;
            this.date = date;
            this.priority = priority;
        }

        public String getTitle() { return title; }

        // Method to get details of the task in string format
        public String getDetails() {
            return title + " - " + description + " - " + priority + " - " + date;
        }
    }

    // Main method to start the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            javaProjectP1 app = new javaProjectP1();  // Create app instance
            app.setVisible(true);                    // Make the app visible
        });
    }
}
