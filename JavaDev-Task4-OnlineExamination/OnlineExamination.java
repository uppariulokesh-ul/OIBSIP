import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class OnlineExamination extends JFrame {
    private String username = "Lokesh";
    private String password = "password123";
    private boolean isLoggedIn = false;
    
    // Exam Data
    private String[] questions = {
        "1. Which of the following is NOT a Java feature?",
        "2. What is the default value of an Object reference in Java?",
        "3. Which package contains the Random class?",
        "4. Which of these keywords is used to inherit a class?",
        "5. Which method is the entry point for any Java program?"
    };
    
    private String[][] options = {
        {"Dynamic", "Architecture Neutral", "Use of pointers", "Object-Oriented"},
        {"0", "null", "undefined", "garbage value"},
        {"java.io", "java.lang", "java.util", "java.net"},
        {"implements", "extends", "inherits", "import"},
        {"start()", "main()", "run()", "init()"}
    };
    
    private int[] correctAnswers = {2, 1, 2, 1, 1}; // 0-based indices (pointers, null, java.util, extends, main)
    private int[] userSelections = {-1, -1, -1, -1, -1};
    
    // Exam parameters
    private int currentQuestionIndex = 0;
    private int timeRemaining = 60; // 60 seconds (1 minute) timer
    private Timer timer;
    private int score = 0;

    // GUI UI Components
    private CardLayout cardLayout;
    private JPanel mainPanel, examPanel;
    
    // Login UI components
    private JTextField txtUser;
    private JPasswordField txtPass;
    
    // Exam UI components
    private JLabel lblQuestion, lblTimer;
    private JRadioButton[] rdoOptions;
    private ButtonGroup btnGroup;
    private JButton btnNext, btnPrev, btnSubmit;

    public OnlineExamination() {
        setTitle("OIBSIP - Online Examination System");
        setSize(520, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        setupLoginScreen();
        setupExamScreen();

        add(mainPanel);
        setVisible(true);
    }

    // LOGIN SCREEN SETUP
    private void setupLoginScreen() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(52, 73, 94));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblHeader = new JLabel("ONLINE EXAMINATION SYSTEM", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 20));
        lblHeader.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(lblHeader, gbc);

        JLabel lblSub = new JLabel("Provide credentials to access your session exam", SwingConstants.CENTER);
        lblSub.setFont(new Font("Arial", Font.ITALIC, 11));
        lblSub.setForeground(new Color(189, 195, 199));
        gbc.gridy = 1;
        loginPanel.add(lblSub, gbc);

        JLabel lblUserLabel = new JLabel("Username (Lokesh):");
        lblUserLabel.setForeground(Color.WHITE);
        gbc.gridy = 2; gbc.gridwidth = 1;
        loginPanel.add(lblUserLabel, gbc);

        txtUser = new JTextField(15);
        gbc.gridx = 1;
        loginPanel.add(txtUser, gbc);

        JLabel lblPassLabel = new JLabel("Password (password123):");
        lblPassLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3;
        loginPanel.add(lblPassLabel, gbc);

        txtPass = new JPasswordField(15);
        gbc.gridx = 1;
        loginPanel.add(txtPass, gbc);

        JButton btnLogin = new JButton("Authenticate / Login");
        btnLogin.setBackground(new Color(39, 174, 96));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        loginPanel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> {
            String u = txtUser.getText().trim();
            String p = new String(txtPass.getPassword());

            if (u.equals(username) && p.equals(password)) {
                isLoggedIn = true;
                cardLayout.show(mainPanel, "Exam");
                startExamTimer();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials check info hints!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.add(loginPanel, "Login");
    }

    // EXAM SCREEN SETUP
    private void setupExamScreen() {
        examPanel = new JPanel(new BorderLayout());
        examPanel.setBackground(new Color(245, 247, 250));

        // Header section (Welcome, Timer and Progress check)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblWelcome = new JLabel("Candidate: " + username, SwingConstants.LEFT);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 14));
        lblWelcome.setForeground(Color.WHITE);
        
        lblTimer = new JLabel("Time Remaining: 60s", SwingConstants.RIGHT);
        lblTimer.setFont(new Font("Arial", Font.BOLD, 14));
        lblTimer.setForeground(new Color(241, 196, 15));

        headerPanel.add(lblWelcome, BorderLayout.WEST);
        headerPanel.add(lblTimer, BorderLayout.EAST);

        // Center Panel Question View setup
        JPanel questionView = new JPanel(new BorderLayout());
        questionView.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblQuestion = new JLabel("Question Title loading...", SwingConstants.LEFT);
        lblQuestion.setFont(new Font("Arial", Font.BOLD, 15));
        lblQuestion.setForeground(new Color(44, 62, 80));
        questionView.add(lblQuestion, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        rdoOptions = new JRadioButton[4];
        btnGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            rdoOptions[i] = new JRadioButton();
            rdoOptions[i].setFont(new Font("Arial", Font.PLAIN, 13));
            btnGroup.add(rdoOptions[i]);
            optionsPanel.add(rdoOptions[i]);
            
            final int index = i;
            rdoOptions[i].addActionListener(e -> {
                userSelections[currentQuestionIndex] = index;
            });
        }
        questionView.add(optionsPanel, BorderLayout.CENTER);

        // Bottom navigation Setup
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPrev = new JButton("<< Previous");
        btnNext = new JButton("Next >>");
        btnSubmit = new JButton("Submit Exam");
        btnSubmit.setBackground(new Color(192, 57, 43));
        btnSubmit.setForeground(Color.WHITE);

        navPanel.add(btnPrev);
        navPanel.add(btnNext);
        navPanel.add(btnSubmit);

        examPanel.add(headerPanel, BorderLayout.NORTH);
        examPanel.add(questionView, BorderLayout.CENTER);
        examPanel.add(navPanel, BorderLayout.SOUTH);

        // Action Listeners for navigation control triggers
        btnPrev.addActionListener(e -> navigateQuestion(-1));
        btnNext.addActionListener(e -> navigateQuestion(1));
        btnSubmit.addActionListener(e -> submitExam());

        mainPanel.add(examPanel, "Exam");
        loadQuestion(0);
    }

    private void startExamTimer() {
        timer = new Timer(1000, e -> {
            timeRemaining--;
            lblTimer.setText("Time Remaining: " + timeRemaining + "s");
            if (timeRemaining <= 10) {
                lblTimer.setForeground(new Color(231, 76, 60)); // Make red on deadline
            }
            if (timeRemaining <= 0) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Time is up! auto-submitting current session...", "Time's Over", JOptionPane.WARNING_MESSAGE);
                submitExam();
            }
        });
        timer.start();
    }

    private void loadQuestion(int index) {
        currentQuestionIndex = index;
        lblQuestion.setText(questions[index]);

        btnGroup.clearSelection();
        for (int i = 0; i < 4; i++) {
            rdoOptions[i].setText(options[index][i]);
            if (userSelections[index] == i) {
                rdoOptions[i].setSelected(true);
            }
        }

        // Handle navigation constraints UI state properties
        btnPrev.setEnabled(index > 0);
        btnNext.setVisible(index < questions.length - 1);
        btnSubmit.setVisible(index == questions.length - 1);
    }

    private void navigateQuestion(int direction) {
        loadQuestion(currentQuestionIndex + direction);
    }

    private void submitExam() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        score = 0;
        for (int i = 0; i < questions.length; i++) {
            if (userSelections[i] == correctAnswers[i]) {
                score += 20; // 20 marks per correct question (Total scale: 100 max)
            }
        }

        String summary = String.format(
            "Exam Completed!\nCandidate Name: %s\nQuestions Attempted: %d / %d\nFinal Score: %d / 100 points",
            username, getAttemptCount(), questions.length, score
        );

        JOptionPane.showMessageDialog(this, summary, "Session Finished", JOptionPane.INFORMATION_MESSAGE);
        
        // Final screen options
        int quitChoice = JOptionPane.showConfirmDialog(this, "Do you want to terminate simulation process?", "Quit Action", JOptionPane.YES_NO_OPTION);
        if (quitChoice == JOptionPane.YES_OPTION) {
            System.exit(0);
        } else {
            // reset logic to Login view safety constraints resets
            isLoggedIn = false;
            currentQuestionIndex = 0;
            timeRemaining = 60;
            userSelections = new int[]{-1, -1, -1, -1, -1};
            txtUser.setText("");
            txtPass.setText("");
            loadQuestion(0);
            cardLayout.show(mainPanel, "Login");
        }
    }

    private int getAttemptCount() {
        int count = 0;
        for (int ans : userSelections) {
            if (ans != -1) count++;
        }
        return count;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OnlineExamination::new);
    }
}
