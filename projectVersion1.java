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
public class simpleAPP extends JFrame {
    private List<Expense> expenses;
    private List<Task> tasks;
    private JTextArea displayArea;

    public simpleAPP() {
        expenses = loadExpenses();
        tasks = loadTasks();

        setTitle("Expense Tracker & To-Do List");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Expenses", createExpensePanel());
        tabbedPane.add("Tasks", createTaskPanel());
        add(tabbedPane, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
            }
        });
    }

    // Panel for Expenses
    private JPanel createExpensePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(4, 2));

        JTextField titleField = new JTextField();
        JTextField amountField = new JTextField();

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);

        JButton addExpenseButton = new JButton("Add Expense");
        formPanel.add(addExpenseButton);
        panel.add(formPanel, BorderLayout.NORTH);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        panel.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        addExpenseButton.addActionListener(e -> {
            try {
                String title = titleField.getText();
                double amount = Double.parseDouble(amountField.getText());

                Expense expense = new Expense(title, amount, new Date());
                expenses.add(expense);
                displayArea.append("Added Expense: " + expense.getDetails() + "\n");

                titleField.setText("");
                amountField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a number.");
            }
        });

        return panel;
    }

    // Panel for Tasks
    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(3, 2));

        JTextField titleField = new JTextField();
        JTextField priorityField = new JTextField();

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Priority (Low/Medium/High):"));
        formPanel.add(priorityField);

        JButton addTaskButton = new JButton("Add Task");
        formPanel.add(addTaskButton);
        panel.add(formPanel, BorderLayout.NORTH);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        panel.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        addTaskButton.addActionListener(e -> {
            String title = titleField.getText();
            String priority = priorityField.getText();

            Task task = new Task(title, priority, new Date());
            tasks.add(task);
            displayArea.append("Added Task: " + task.getDetails() + "\n");

            titleField.setText("");
            priorityField.setText("");
        });

        return panel;
    }

    // Save and Load Data for Persistence
    private void saveData() {
        new Thread(() -> {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("expenses.ser"))) {
                oos.writeObject(expenses);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("tasks.ser"))) {
                oos.writeObject(tasks);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private List<Expense> loadExpenses() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("expenses.ser"))) {
            return (List<Expense>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private List<Task> loadTasks() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("tasks.ser"))) {
            return (List<Task>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    // Expense Class
    static class Expense implements Serializable {
        private String title;
        private double amount;
        private Date date;

        public Expense(String title, double amount, Date date) {
            this.title = title;
            this.amount = amount;
            this.date = date;
        }

        public String getDetails() {
            return title + " - " + amount + " - " + date;
        }
    }

    // Task Class
    static class Task implements Serializable {
        private String title;
        private String priority;
        private Date date;

        public Task(String title, String priority, Date date) {
            this.title = title;
            this.priority = priority;
            this.date = date;
        }

        public String getDetails() {
            return title + " - " + priority + " - " + date;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            simpleAPP app = new simpleAPP();
            app.setVisible(true);
        });
    }
}

