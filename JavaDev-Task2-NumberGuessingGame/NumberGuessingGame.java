import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class NumberGuessingGame extends JFrame {
    private int targetNumber;
    private int attemptsLeft;
    private int currentRound = 1;
    private int totalScore = 0;
    private final int MAX_ATTEMPTS = 5;
    private final int TOTAL_ROUNDS = 3;

    private JLabel lblRound, lblAttempts, lblScore, lblFeedback;
    private JTextField txtGuess;
    private JButton btnGuess, btnRestart;

    public NumberGuessingGame() {
        setTitle("OIBSIP - Number Guessing Game");
        setSize(460, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(new Color(41, 128, 185));
        
        JLabel title = new JLabel("Number Guessing Game (1 - 100)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        headerPanel.add(title);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        infoPanel.setOpaque(false);
        lblRound = new JLabel("Round: 1/" + TOTAL_ROUNDS);
        lblRound.setForeground(Color.WHITE);
        lblAttempts = new JLabel("Attempts Left: " + MAX_ATTEMPTS);
        lblAttempts.setForeground(Color.WHITE);
        lblScore = new JLabel("Total Score: 0");
        lblScore.setForeground(Color.WHITE);

        infoPanel.add(lblRound);
        infoPanel.add(lblAttempts);
        infoPanel.add(lblScore);
        headerPanel.add(infoPanel);

        // Center Panel (Game Interaction)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblPrompt = new JLabel("Enter your guess (1-100):", SwingConstants.CENTER);
        lblPrompt.setFont(new Font("Arial", Font.PLAIN, 15));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(lblPrompt, gbc);

        txtGuess = new JTextField(10);
        txtGuess.setFont(new Font("Arial", Font.BOLD, 16));
        txtGuess.setHorizontalAlignment(JTextField.CENTER);
        gbc.gridy = 1; gbc.gridwidth = 1;
        centerPanel.add(txtGuess, gbc);

        btnGuess = new JButton("Submit Guess");
        btnGuess.setBackground(new Color(39, 174, 96));
        btnGuess.setForeground(Color.WHITE);
        btnGuess.setFocusPainted(false);
        gbc.gridx = 1;
        centerPanel.add(btnGuess, gbc);

        lblFeedback = new JLabel("Guess a number between 1 and 100 to begin!", SwingConstants.CENTER);
        lblFeedback.setFont(new Font("Arial", Font.BOLD, 13));
        lblFeedback.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        centerPanel.add(lblFeedback, gbc);

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        btnRestart = new JButton("Play Again");
        btnRestart.setEnabled(false);
        btnRestart.addActionListener(e -> startFullGame());
        bottomPanel.add(btnRestart);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        btnGuess.addActionListener(e -> processGuess());
        txtGuess.addActionListener(e -> processGuess());

        startFullGame();
    }

    private void startFullGame() {
        currentRound = 1;
        totalScore = 0;
        btnRestart.setEnabled(false);
        startRound();
    }

    private void startRound() {
        targetNumber = new Random().nextInt(100) + 1;
        attemptsLeft = MAX_ATTEMPTS;
        updateDashboard();
        lblFeedback.setText("New round started! Guess a number between 1 and 100.");
        lblFeedback.setForeground(new Color(52, 73, 94));
        txtGuess.setText("");
        txtGuess.setEnabled(true);
        btnGuess.setEnabled(true);
        txtGuess.requestFocus();
    }

    private void updateDashboard() {
        lblRound.setText("Round: " + currentRound + "/" + TOTAL_ROUNDS);
        lblAttempts.setText("Attempts Left: " + attemptsLeft);
        lblScore.setText("Total Score: " + totalScore);
    }

    private void processGuess() {
        String input = txtGuess.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int guess;
        try {
            guess = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Only integer numbers are allowed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (guess < 1 || guess > 100) {
            JOptionPane.showMessageDialog(this, "Number must be between 1 and 100!", "Out of Range", JOptionPane.WARNING_MESSAGE);
            return;
        }

        attemptsLeft--;

        if (guess == targetNumber) {
            int roundPoints = attemptsLeft * 20 + 20; // Maximum 100 points
            totalScore += roundPoints;
            lblFeedback.setText("Correct! Number was " + targetNumber + ". +" + roundPoints + " pts!");
            lblFeedback.setForeground(new Color(39, 174, 96));
            endRound(true);
        } else if (attemptsLeft == 0) {
            lblFeedback.setText("Round over! The correct number was " + targetNumber + ".");
            lblFeedback.setForeground(Color.RED);
            endRound(false);
        } else if (guess > targetNumber) {
            lblFeedback.setText("Too High! Try a smaller number.");
            lblFeedback.setForeground(new Color(211, 84, 0));
        } else {
            lblFeedback.setText("Too Low! Try a bigger number.");
            lblFeedback.setForeground(new Color(211, 84, 0));
        }

        updateDashboard();
        txtGuess.setText("");
        txtGuess.requestFocus();
    }

    private void endRound(boolean won) {
        txtGuess.setEnabled(false);
        btnGuess.setEnabled(false);

        if (currentRound < TOTAL_ROUNDS) {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    (won ? "Well done! " : "Oops! ") + "Ready for Round " + (currentRound + 1) + "?",
                    "Round Complete",
                    JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.YES_OPTION) {
                currentRound++;
                startRound();
            } else {
                finishGame();
            }
        } else {
            finishGame();
        }
    }

    private void finishGame() {
        updateDashboard();
        txtGuess.setEnabled(false);
        btnGuess.setEnabled(false);
        btnRestart.setEnabled(true);
        JOptionPane.showMessageDialog(
                this,
                "Game Over!\nFinal Total Score: " + totalScore + " points.",
                "Game Finished",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NumberGuessingGame().setVisible(true));
    }
}
