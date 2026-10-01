import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ExpenseTracker {

    // ==========================================
    // Expense data
    // ==========================================

    static ArrayList<String> titles = new ArrayList<>();
    static ArrayList<String> categories = new ArrayList<>();
    static ArrayList<Double> amounts = new ArrayList<>();
    static ArrayList<String> dates = new ArrayList<>();

    static final String FILE = "expenses.txt";

    // ==========================================
    // GUI components
    // ==========================================

    static JFrame frame;

    static JTextField titleField;
    static JTextField amountField;
    static JTextField dateField;

    static JComboBox<String> categoryBox;

    static JTable table;
    static DefaultTableModel model;

    // ==========================================
    // Main
    // ==========================================

    public static void main(String[] args) {

        frame = new JFrame("Expense Tracker");

        frame.setSize(900, 600);

        frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        frame.setLocationRelativeTo(null);

        // ==========================================
        // Title
        // ==========================================

        JLabel heading =
                new JLabel(
                        "EXPENSE TRACKER",
                        SwingConstants.CENTER
                );

        heading.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        26
                )
        );

        // ==========================================
        // Input panel
        // ==========================================

        JPanel inputPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                5,
                                10,
                                10
                        )
                );

        inputPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        15,
                        15,
                        15
                )
        );

        inputPanel.add(
                new JLabel("Expense Title")
        );

        inputPanel.add(
                new JLabel("Category")
        );

        inputPanel.add(
                new JLabel("Amount")
        );

        inputPanel.add(
                new JLabel("Date")
        );

        inputPanel.add(
                new JLabel("")
        );

        titleField =
                new JTextField();

        categoryBox =
                new JComboBox<>(
                        new String[]{
                                "Food",
                                "Travel",
                                "Shopping",
                                "Education",
                                "Bills",
                                "Other"
                        }
                );

        amountField =
                new JTextField();

        dateField =
                new JTextField(
                        LocalDate.now().toString()
                );

        JButton addButton =
                new JButton(
                        "Add Expense"
                );

        inputPanel.add(titleField);
        inputPanel.add(categoryBox);
        inputPanel.add(amountField);
        inputPanel.add(dateField);
        inputPanel.add(addButton);

        // ==========================================
        // Table
        // ==========================================

        model =
                new DefaultTableModel(
                        new String[]{
                                "Title",
                                "Category",
                                "Amount",
                                "Date"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        table =
                new JTable(model);

        table.setRowHeight(30);

        JScrollPane scrollPane =
                new JScrollPane(table);

        // ==========================================
        // Buttons
        // ==========================================

        JButton deleteButton =
                new JButton(
                        "Delete Expense"
                );

        JButton totalButton =
                new JButton(
                        "Total Expenses"
                );

        JButton analyticsButton =
                new JButton(
                        "Category Analytics"
                );

        JButton monthButton =
                new JButton(
                        "Current Month"
                );

        JButton allButton =
                new JButton(
                        "Show All"
                );

        JPanel buttonPanel =
                new JPanel();

        buttonPanel.add(deleteButton);
        buttonPanel.add(totalButton);
        buttonPanel.add(analyticsButton);
        buttonPanel.add(monthButton);
        buttonPanel.add(allButton);

        // ==========================================
        // Add Expense
        // ==========================================

        addButton.addActionListener(e -> {

            String title =
                    titleField
                            .getText()
                            .trim();

            String category =
                    categoryBox
                            .getSelectedItem()
                            .toString();

            String amountText =
                    amountField
                            .getText()
                            .trim();

            String date =
                    dateField
                            .getText()
                            .trim();

            // Check empty fields
            if (title.isEmpty()
                    || amountText.isEmpty()
                    || date.isEmpty()) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please fill all fields."
                );

                return;
            }

            // Check amount
            double amount;

            try {

                amount =
                        Double.parseDouble(
                                amountText
                        );

                if (amount <= 0) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Amount must be greater than 0."
                    );

                    return;
                }

            } catch (NumberFormatException error) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Enter a valid amount."
                );

                return;
            }

            // Check date
            try {

                LocalDate.parse(date);

            } catch (Exception error) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Use date format:\nYYYY-MM-DD\n\nExample: 2026-10-15"
                );

                return;
            }

            // Add expense
            titles.add(title);
            categories.add(category);
            amounts.add(amount);
            dates.add(date);

            saveExpenses();

            titleField.setText("");
            amountField.setText("");

            showAll();
        });

        // ==========================================
        // Delete Expense
        // ==========================================

        deleteButton.addActionListener(e -> {

            int row =
                    table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Select an expense first."
                );

                return;
            }

            String selectedTitle =
                    model
                            .getValueAt(
                                    row,
                                    0
                            )
                            .toString();

            for (int i = 0;
                 i < titles.size();
                 i++) {

                if (titles.get(i)
                        .equals(selectedTitle)) {

                    titles.remove(i);
                    categories.remove(i);
                    amounts.remove(i);
                    dates.remove(i);

                    break;
                }
            }

            saveExpenses();

            showAll();
        });

        // ==========================================
        // Total Expenses
        // ==========================================

        totalButton.addActionListener(e -> {

            double total = 0;

            for (double amount : amounts) {

                total += amount;
            }

            JOptionPane.showMessageDialog(
                    frame,
                    String.format(
                            "Total Expenses: Rs. %.2f",
                            total
                    ),
                    "Expense Summary",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // ==========================================
        // Category Analytics
        // ==========================================

        analyticsButton.addActionListener(e -> {

            double food = 0;
            double travel = 0;
            double shopping = 0;
            double education = 0;
            double bills = 0;
            double other = 0;

            for (int i = 0;
                 i < categories.size();
                 i++) {

                String category =
                        categories.get(i);

                double amount =
                        amounts.get(i);

                if (category.equals("Food")) {

                    food += amount;

                } else if (category.equals("Travel")) {

                    travel += amount;

                } else if (category.equals("Shopping")) {

                    shopping += amount;

                } else if (category.equals("Education")) {

                    education += amount;

                } else if (category.equals("Bills")) {

                    bills += amount;

                } else {

                    other += amount;
                }
            }

            StringBuilder report =
                    new StringBuilder();

            report.append(
                    "CATEGORY ANALYTICS\n"
            );

            report.append(
                    "============================\n\n"
            );

            report.append(
                    "Food       : Rs. "
            ).append(
                    String.format(
                            "%.2f",
                            food
                    )
            ).append("\n");

            report.append(
                    "Travel     : Rs. "
            ).append(
                    String.format(
                            "%.2f",
                            travel
                    )
            ).append("\n");

            report.append(
                    "Shopping   : Rs. "
            ).append(
                    String.format(
                            "%.2f",
                            shopping
                    )
            ).append("\n");

            report.append(
                    "Education  : Rs. "
            ).append(
                    String.format(
                            "%.2f",
                            education
                    )
            ).append("\n");

            report.append(
                    "Bills      : Rs. "
            ).append(
                    String.format(
                            "%.2f",
                            bills
                    )
            ).append("\n");

            report.append(
                    "Other      : Rs. "
            ).append(
                    String.format(
                            "%.2f",
                            other
                    )
            ).append("\n");

            report.append(
                    "\n============================"
            );

            JOptionPane.showMessageDialog(
                    frame,
                    report.toString(),
                    "Category Analytics",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // ==========================================
        // Current Month
        // ==========================================

        monthButton.addActionListener(e -> {

            String currentMonth =
                    LocalDate.now()
                            .toString()
                            .substring(
                                    0,
                                    7
                            );

            double total = 0;

            int count = 0;

            for (int i = 0;
                 i < dates.size();
                 i++) {

                if (dates.get(i)
                        .startsWith(
                                currentMonth
                        )) {

                    total += amounts.get(i);

                    count++;
                }
            }

            JOptionPane.showMessageDialog(
                    frame,
                    "Current Month: "
                            + currentMonth
                            + "\n\n"
                            + "Number of Expenses: "
                            + count
                            + "\n"
                            + String.format(
                                    "Monthly Total: Rs. %.2f",
                                    total
                            ),
                    "Monthly Analytics",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // ==========================================
        // Show All
        // ==========================================

        allButton.addActionListener(
                e -> showAll()
        );

        // ==========================================
        // Load saved expenses
        // ==========================================

        loadExpenses();

        showAll();

        // ==========================================
        // Add components
        // ==========================================

        frame.setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );

        frame.add(
                heading,
                BorderLayout.NORTH
        );

        JPanel center =
                new JPanel(
                        new BorderLayout()
                );

        center.add(
                inputPanel,
                BorderLayout.NORTH
        );

        center.add(
                scrollPane,
                BorderLayout.CENTER
        );

        frame.add(
                center,
                BorderLayout.CENTER
        );

        frame.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        frame.setVisible(true);
    }

    // ==========================================
    // Show all expenses
    // ==========================================

    static void showAll() {

        model.setRowCount(0);

        for (int i = 0;
             i < titles.size();
             i++) {

            model.addRow(
                    new Object[]{
                            titles.get(i),
                            categories.get(i),
                            String.format(
                                    "Rs. %.2f",
                                    amounts.get(i)
                            ),
                            dates.get(i)
                    }
            );
        }
    }

    // ==========================================
    // Save expenses
    // ==========================================

    static void saveExpenses() {

        try {

            PrintWriter writer =
                    new PrintWriter(
                            new FileWriter(FILE)
                    );

            for (int i = 0;
                 i < titles.size();
                 i++) {

                writer.println(
                        titles.get(i)
                                + "|"
                                + categories.get(i)
                                + "|"
                                + amounts.get(i)
                                + "|"
                                + dates.get(i)
                );
            }

            writer.close();

        } catch (IOException error) {

            JOptionPane.showMessageDialog(
                    null,
                    "Unable to save expenses."
            );
        }
    }

    // ==========================================
    // Load expenses
    // ==========================================

    static void loadExpenses() {

        File file =
                new File(FILE);

        if (!file.exists()) {

            return;
        }

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(file)
                    );

            String line;

            while ((line =
                    reader.readLine()) != null) {

                String[] data =
                        line.split(
                                "\\|"
                        );

                if (data.length == 4) {

                    titles.add(
                            data[0]
                    );

                    categories.add(
                            data[1]
                    );

                    amounts.add(
                            Double.parseDouble(
                                    data[2]
                            )
                    );

                    dates.add(
                            data[3]
                    );
                }
            }

            reader.close();

        } catch (Exception error) {

            System.out.println(
                    "Unable to load expenses."
            );
        }
    }
}