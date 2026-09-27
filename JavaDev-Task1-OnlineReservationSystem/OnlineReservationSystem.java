import javax.swing.*;
import java.awt.*;
import java.util.*;

public class OnlineReservationSystem {
    private static Map<Integer, Reservation> reservationsMap = new HashMap<>();
    private static Map<Integer, String> trainCatalog = new HashMap<>();

    public static void main(String[] args) {
        initData();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    private static void initData() {
        trainCatalog.put(12727, "Godavari Express");
        trainCatalog.put(12759, "Charminar Express");
        trainCatalog.put(20833, "Vande Bharat Express");
        trainCatalog.put(12760, "Tirupati Express");
    }

    static class Reservation {
        int pnr;
        String name;
        int trainNo;
        String trainName;
        String classType;
        String date;
        String source;
        String destination;

        public Reservation(int pnr, String name, int trainNo, String trainName, String classType, String date, String source, String destination) {
            this.pnr = pnr;
            this.name = name;
            this.trainNo = trainNo;
            this.trainName = trainName;
            this.classType = classType;
            this.date = date;
            this.source = source;
            this.destination = destination;
        }
    }

    // 1. Login Screen
    static class LoginFrame extends JFrame {
        private JTextField userField = new JTextField(15);
        private JPasswordField passField = new JPasswordField(15);

        public LoginFrame() {
            setTitle("Train Reservation - Login");
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
                JOptionPane.showMessageDialog(this, "Please enter username and password!", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (user.equalsIgnoreCase("admin") && pass.equals("admin123")) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();
                new MainAppFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Access Denied: Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 2. Main Dashboard
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

    // 3. Reservation Tab
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
            formPanel.add(new JLabel("Train Name:")); formPanel.add(txtTrainName);
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
                if (trainCatalog.containsKey(trainNo)) {
                    txtTrainName.setText(trainCatalog.get(trainNo));
                } else {
                    txtTrainName.setText("Special Express");
                }
            } catch (NumberFormatException e) {
                txtTrainName.setText("Numbers Only");
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

            int pnr = 100000 + new Random().nextInt(900000);
            Reservation r = new Reservation(pnr, name, trainNo, tname, classType, date, src, dst);
            reservationsMap.put(pnr, r);

            String confirmationMessage = String.format(
                    "Booking Confirmed!\n\n" +
                    "PNR Number: %d\n" +
                    "Passenger: %s\n" +
                    "Train: %d - %s\n" +
                    "Class: %s\n" +
                    "Date: %s\n" +
                    "Route: %s to %s",
                    pnr, name, trainNo, tname, classType, date, src, dst);

            JOptionPane.showMessageDialog(this, confirmationMessage, "Confirmation", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
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

    // 4. Cancellation Tab
    static class CancellationPanel extends JPanel {
        private JTextField txtPnr = new JTextField(12);
        private JTextArea txtDetails = new JTextArea(10, 30);
        private JButton btnCancel = new JButton("Confirm Cancellation");

        public CancellationPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton btnFetch = new JButton("Fetch Booking");
            topPanel.add(new JLabel("Enter PNR Number:"));
            topPanel.add(txtPnr);
            topPanel.add(btnFetch);

            txtDetails.setEditable(false);
            txtDetails.setFont(new Font("Monospaced", Font.PLAIN, 13));
            btnCancel.setEnabled(false);

            btnFetch.addActionListener(e -> fetchDetails());
            btnCancel.addActionListener(e -> cancelBooking());

            add(topPanel, BorderLayout.NORTH);
            add(new JScrollPane(txtDetails), BorderLayout.CENTER);
            add(btnCancel, BorderLayout.SOUTH);
        }

        private void fetchDetails() {
            String pnrStr = txtPnr.getText().trim();
            if (pnrStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a PNR number.");
                return;
            }

            try {
                int pnr = Integer.parseInt(pnrStr);
                if (reservationsMap.containsKey(pnr)) {
                    Reservation r = reservationsMap.get(pnr);
                    txtDetails.setText(
                            "================ BOOKING DETAILS ================\n" +
                            " PNR Number      : " + r.pnr + "\n" +
                            " Passenger Name  : " + r.name + "\n" +
                            " Train           : " + r.trainNo + " (" + r.trainName + ")\n" +
                            " Class           : " + r.classType + "\n" +
                            " Date of Journey : " + r.date + "\n" +
                            " Route           : " + r.source + " to " + r.destination + "\n" +
                            "================================================="
                    );
                    btnCancel.setEnabled(true);
                } else {
                    txtDetails.setText("No reservation found matching PNR: " + pnr);
                    btnCancel.setEnabled(false);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "PNR must be numeric!");
            }
        }

        private void cancelBooking() {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to cancel this booking?",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                int pnr = Integer.parseInt(txtPnr.getText().trim());
                reservationsMap.remove(pnr);
                JOptionPane.showMessageDialog(this, "Booking successfully cancelled.");
                txtDetails.setText("");
                txtPnr.setText("");
                btnCancel.setEnabled(false);
            }
        }
    }
}
