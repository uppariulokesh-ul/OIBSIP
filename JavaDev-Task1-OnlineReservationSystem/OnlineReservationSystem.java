import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Random;

public class OnlineReservationSystem {
    private static final String DB_URL = "jdbc:sqlite:reservation.db";

    public static void main(String[] args) {
        initDatabase();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    private static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found!");
        }
        return DriverManager.getConnection(DB_URL);
    }

    private static void initDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, password TEXT)");
            stmt.execute("INSERT OR IGNORE INTO users VALUES ('admin', 'admin123')");

            stmt.execute("CREATE TABLE IF NOT EXISTS trains (train_no INTEGER PRIMARY KEY, train_name TEXT)");
            stmt.execute("INSERT OR IGNORE INTO trains VALUES (12727, 'Godavari Express')");
            stmt.execute("INSERT OR IGNORE INTO trains VALUES (12759, 'Charminar Express')");
            stmt.execute("INSERT OR IGNORE INTO trains VALUES (20833, 'Vande Bharat Express')");

            stmt.execute("CREATE TABLE IF NOT EXISTS reservations (" +
                    "pnr INTEGER PRIMARY KEY, " +
                    "passenger_name TEXT, " +
                    "train_no INTEGER, " +
                    "train_name TEXT, " +
                    "class_type TEXT, " +
                    "journey_date TEXT, " +
                    "source TEXT, " +
                    "destination TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Login Form
    static class LoginFrame extends JFrame {
        private JTextField userField = new JTextField(15);
        private JPasswordField passField = new JPasswordField(15);

        public LoginFrame() {
            setTitle("Reservation System - Login");
            setSize(360, 200);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            panel.add(new JLabel("Username:"));
            panel.add(userField);
            panel.add(new JLabel("Password:"));
            panel.add(passField);

            JButton loginBtn = new JButton("Login");
            panel.add(new JLabel(""));
            panel.add(loginBtn);

            loginBtn.addActionListener(e -> authenticate());
            add(panel);
        }

        private void authenticate() {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
                ps.setString(1, user);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    dispose();
                    new MainAppFrame().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Access Denied: Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            }
        }
    }

    // Main Reservation & Cancellation Window
    static class MainAppFrame extends JFrame {
        public MainAppFrame() {
            setTitle("Online Reservation & Cancellation System");
            setSize(620, 520);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Book Ticket", new ReservationPanel());
            tabs.addTab("Cancel Booking", new CancellationPanel());

            add(tabs);
        }
    }

    // Reservation Tab Panel
    static class ReservationPanel extends JPanel {
        private JTextField txtName = new JTextField();
        private JTextField txtTrainNo = new JTextField();
        private JTextField txtTrainName = new JTextField();
        private JComboBox<String> cmbClass = new JComboBox<>(new String[]{"Sleeper", "3rd AC", "2nd AC", "1st AC"});
        private JTextField txtDate = new JTextField("YYYY-MM-DD");
        private JTextField txtSource = new JTextField();
        private JTextField txtDest = new JTextField();

        public ReservationPanel() {
            setLayout(new BorderLayout());
            JPanel formPanel = new JPanel(new GridLayout(8, 2, 8, 8));
            formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            txtTrainName.setEditable(false);

            txtTrainNo.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    populateTrainName();
                }
            });

            formPanel.add(new JLabel("Passenger Name:")); formPanel.add(txtName);
            formPanel.add(new JLabel("Train Number (e.g. 12727):")); formPanel.add(txtTrainNo);
            formPanel.add(new JLabel("Train Name (Auto-filled):")); formPanel.add(txtTrainName);
            formPanel.add(new JLabel("Class Type:")); formPanel.add(cmbClass);
            formPanel.add(new JLabel("Date of Journey:")); formPanel.add(txtDate);
            formPanel.add(new JLabel("Source Station:")); formPanel.add(txtSource);
            formPanel.add(new JLabel("Destination Station:")); formPanel.add(txtDest);

            JButton btnBook = new JButton("Book Ticket");
            btnBook.addActionListener(e -> handleBooking());

            formPanel.add(new JLabel(""));
            formPanel.add(btnBook);

            add(formPanel, BorderLayout.CENTER);
        }

        private void populateTrainName() {
            String tnoStr = txtTrainNo.getText().trim();
            if (tnoStr.isEmpty()) return;

            try {
                int trainNo = Integer.parseInt(tnoStr);
                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement("SELECT train_name FROM trains WHERE train_no = ?")) {
                    ps.setInt(1, trainNo);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        txtTrainName.setText(rs.getString("train_name"));
                    } else {
                        txtTrainName.setText("Train Not Found");
                    }
                }
            } catch (NumberFormatException e) {
                txtTrainName.setText("Numeric Train No Only");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void handleBooking() {
            String name = txtName.getText().trim();
            String tnoStr = txtTrainNo.getText().trim();
            String tname = txtTrainName.getText().trim();
            String classType = (String) cmbClass.getSelectedItem();
            String date = txtDate.getText().trim();
            String src = txtSource.getText().trim();
            String dst = txtDest.getText().trim();

            if (name.isEmpty() || tnoStr.isEmpty() || date.isEmpty() || src.isEmpty() || dst.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int trainNo;
            try {
                trainNo = Integer.parseInt(tnoStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Train Number must be numeric!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                JOptionPane.showMessageDialog(this, "Use format: YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int pnr = 100000 + new Random().nextInt(900000);

            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO reservations VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, pnr);
                ps.setString(2, name);
                ps.setInt(3, trainNo);
                ps.setString(4, tname);
                ps.setString(5, classType);
                ps.setString(6, date);
                ps.setString(7, src);
                ps.setString(8, dst);
                ps.executeUpdate();

                String msg = String.format("Booking Confirmed!\nPNR: %d\nName: %s\nTrain: %d - %s\nRoute: %s to %s",
                        pnr, name, trainNo, tname, src, dst);
                JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Booking Failed: " + ex.getMessage());
            }
        }

        private void clearFields() {
            txtName.setText("");
            txtTrainNo.setText("");
            txtTrainName.setText("");
            txtDate.setText("YYYY-MM-DD");
            txtSource.setText("");
            txtDest.setText("");
        }
    }

    // Cancellation Tab Panel
    static class CancellationPanel extends JPanel {
        private JTextField txtPnr = new JTextField(12);
        private JTextArea txtDetails = new JTextArea(10, 30);
        private JButton btnCancel = new JButton("Confirm Cancellation");

        public CancellationPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton btnFetch = new JButton("Fetch Booking");
            topPanel.add(new JLabel("Enter PNR:"));
            topPanel.add(txtPnr);
            topPanel.add(btnFetch);

            txtDetails.setEditable(false);
            btnCancel.setEnabled(false);

            btnFetch.addActionListener(e -> fetchDetails());
            btnCancel.addActionListener(e -> cancelBooking());

            add(topPanel, BorderLayout.NORTH);
            add(new JScrollPane(txtDetails), BorderLayout.CENTER);
            add(btnCancel, BorderLayout.SOUTH);
        }

        private void fetchDetails() {
            String pnrStr = txtPnr.getText().trim();
            if (pnrStr.isEmpty()) return;

            try {
                int pnr = Integer.parseInt(pnrStr);
                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement("SELECT * FROM reservations WHERE pnr = ?")) {
                    ps.setInt(1, pnr);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        txtDetails.setText(
                                "PNR: " + rs.getInt("pnr") + "\n" +
                                "Name: " + rs.getString("passenger_name") + "\n" +
                                "Train: " + rs.getInt("train_no") + " (" + rs.getString("train_name") + ")\n" +
                                "Class: " + rs.getString("class_type") + "\n" +
                                "Date: " + rs.getString("journey_date") + "\n" +
                                "Route: " + rs.getString("source") + " to " + rs.getString("destination")
                        );
                        btnCancel.setEnabled(true);
                    } else {
                        txtDetails.setText("No reservation found for PNR: " + pnr);
                        btnCancel.setEnabled(false);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter valid numeric PNR");
            }
        }

        private void cancelBooking() {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM reservations WHERE pnr = ?")) {
                    ps.setInt(1, Integer.parseInt(txtPnr.getText().trim()));
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Cancelled Successfully!");
                    txtDetails.setText("");
                    txtPnr.setText("");
                    btnCancel.setEnabled(false);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        }
    }
}