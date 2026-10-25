import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ATMInterface extends JFrame {
    private double balance = 5000.0; // Initial balance
    private final String correctPin = "1234"; // Default PIN
    private final ArrayList<String> transactionHistory = new ArrayList<>();

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField txtUserId;
    private JPasswordField txtPin;
    private JLabel lblBalance, lblStatus;
    private JTextArea txtHistory;

    public ATMInterface() {
        setTitle("OIBSIP - ATM Interface");
        setSize(480, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Transaction History initialization
        transactionHistory.add("Account Opened with initial Balance: 5000.0 INR");

        // UI Panels Setup
        setupLoginScreen();
        setupDashboardScreen();

        add(mainPanel);
        setVisible(true);
    }

    // SCREEN 1: LOGIN
    private void setupLoginScreen() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(41, 128, 185));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("ATM INTERFACE", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(lblTitle, gbc);

        JLabel lblSubtitle = new JLabel("Please enter credentials below", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Arial", Font.ITALIC, 12));
        lblSubtitle.setForeground(new Color(236, 240, 241));
        gbc.gridy = 1;
        loginPanel.add(lblSubtitle, gbc);

        JLabel lblUser = new JLabel("User ID:");
        lblUser.setForeground(Color.WHITE);
        gbc.gridy = 2; gbc.gridwidth = 1;
        loginPanel.add(lblUser, gbc);

        txtUserId = new JTextField(12);
        gbc.gridx = 1;
        loginPanel.add(txtUserId, gbc);

        JLabel lblPass = new JLabel("PIN (1234):");
        lblPass.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3;
        loginPanel.add(lblPass, gbc);

        txtPin = new JPasswordField(12);
        gbc.gridx = 1;
        loginPanel.add(txtPin, gbc);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        loginPanel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> {
            String uid = txtUserId.getText().trim();
            String pin = new String(txtPin.getPassword());

            if (uid.isEmpty() || pin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (pin.equals(correctPin)) {
                cardLayout.show(mainPanel, "Dashboard");
                updateDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid PIN! Try '1234'", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.add(loginPanel, "Login");
    }

    // SCREEN 2: DASHBOARD
    private void setupDashboardScreen() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(new Color(236, 240, 241));

        // Header View
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(new Color(44, 62, 80));
        JLabel lblWelcome = new JLabel("Welcome back to ATM Services", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        lblWelcome.setForeground(Color.WHITE);
        lblBalance = new JLabel("Balance: 5000.0 INR", SwingConstants.CENTER);
        lblBalance.setFont(new Font("Arial", Font.BOLD, 14));
        lblBalance.setForeground(new Color(46, 204, 113));
        topPanel.add(lblWelcome);
        topPanel.add(lblBalance);

        // Center Operations
        JPanel optPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton btnHistory = new JButton("Transaction History");
        JButton btnWithdraw = new JButton("Withdraw");
        JButton btnDeposit = new JButton("Deposit");
        JButton btnTransfer = new JButton("Transfer");

        optPanel.add(btnHistory);
        optPanel.add(btnWithdraw);
        optPanel.add(btnDeposit);
        optPanel.add(btnTransfer);

        // Output Status
        lblStatus = new JLabel("Ready for transactions...", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Arial", Font.ITALIC, 13));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Bottom Exit Options
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnQuit = new JButton("Quit / Exit");
        btnQuit.setBackground(new Color(192, 57, 43));
        btnQuit.setForeground(Color.WHITE);
        bottomPanel.add(btnQuit);

        dashboard.add(topPanel, BorderLayout.NORTH);
        dashboard.add(optPanel, BorderLayout.CENTER);
        dashboard.add(lblStatus, BorderLayout.SOUTH);
        dashboard.add(bottomPanel, BorderLayout.PAGE_END);

        // Button Actions
        btnWithdraw.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter amount to Withdraw (INR):", "Withdraw Option", JOptionPane.PLAIN_MESSAGE);
            if (input != null && !input.isEmpty()) {
                try {
                    double amount = Double.parseDouble(input);
                    if (amount > 0 && amount <= balance) {
                        balance -= amount;
                        transactionHistory.add("Withdrew: " + amount + " INR");
                        lblStatus.setText("Successfully withdrew " + amount + " INR!");
                        updateDashboard();
                    } else {
                        JOptionPane.showMessageDialog(this, "Insufficient balance or invalid amount!", "Withdraw Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter numeric amounts only!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnDeposit.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter amount to Deposit (INR):", "Deposit Option", JOptionPane.PLAIN_MESSAGE);
            if (input != null && !input.isEmpty()) {
                try {
                    double amount = Double.parseDouble(input);
                    if (amount > 0) {
                        balance += amount;
                        transactionHistory.add("Deposited: " + amount + " INR");
                        lblStatus.setText("Successfully deposited " + amount + " INR!");
                        updateDashboard();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid deposit amount!", "Deposit Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter numeric amounts only!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnTransfer.addActionListener(e -> {
            String targetId = JOptionPane.showInputDialog(this, "Enter recipient Account / User ID:", "Transfer Target", JOptionPane.PLAIN_MESSAGE);
            if (targetId != null && !targetId.trim().isEmpty()) {
                String input = JOptionPane.showInputDialog(this, "Enter transfer Amount (INR):", "Transfer Amount", JOptionPane.PLAIN_MESSAGE);
                if (input != null && !input.isEmpty()) {
                    try {
                        double amount = Double.parseDouble(input);
                        if (amount > 0 && amount <= balance) {
                            balance -= amount;
                            transactionHistory.add("Transferred: " + amount + " INR to user: " + targetId.trim());
                            lblStatus.setText("Successfully transferred " + amount + " to ID: " + targetId.trim() + "!");
                            updateDashboard();
                        } else {
                            JOptionPane.showMessageDialog(this, "Insufficient balance or invalid transfer amount!", "Transfer Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Please enter numeric amounts only!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnHistory.addActionListener(e -> {
            showHistoryDialog();
        });

        btnQuit.addActionListener(e -> {
            int reply = JOptionPane.showConfirmDialog(this, "Do you want to terminate current session?", "Log out Options", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                // Clear UI values for session safety
                txtUserId.setText("");
                txtPin.setText("");
                lblStatus.setText("Ready for transactions...");
                cardLayout.show(mainPanel, "Login");
            }
        });

        mainPanel.add(dashboard, "Dashboard");
    }

    private void showHistoryDialog() {
        JDialog historyDialog = new JDialog(this, "Detailed Transaction History", true);
        historyDialog.setSize(380, 260);
        historyDialog.setLocationRelativeTo(this);

        txtHistory = new JTextArea();
        txtHistory.setEditable(false);
        txtHistory.setFont(new Font("Monospaced", Font.PLAIN, 12));

        StringBuilder sb = new StringBuilder();
        sb.append("===== ATM TRANSACTION HISTORY =====\n");
        for (String record : transactionHistory) {
            sb.append("- ").append(record).append("\n");
        }
        sb.append("===================================\n");
        txtHistory.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(txtHistory);
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> historyDialog.dispose());

        historyDialog.add(scrollPane, BorderLayout.CENTER);
        historyDialog.add(btnClose, BorderLayout.SOUTH);
        historyDialog.setVisible(true);
    }

    private void updateDashboard() {
        lblBalance.setText("Current Balance: " + balance + " INR");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ATMInterface::new);
    }
}
