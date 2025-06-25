import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private final JTextField userField = new JTextField(15);
    private final JPasswordField passField = new JPasswordField(15);
    private final JLabel status = new JLabel(" ");

    private static final Color DARK_BG    = new Color(30, 30, 30);
    private static final Color FIELD_BG   = new Color(50, 50, 50);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color BTN_BG     = new Color(70, 130, 180);

    public LoginFrame() {
        super("Hotel Booking – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 240);
        setLocationRelativeTo(null);
        buildUI();
        getContentPane().setBackground(DARK_BG);
    }

    private void buildUI() {
        JLabel title = new JLabel("Hotel Booking System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(TEXT_COLOR);

        JLabel userLbl = new JLabel("Username:");
        JLabel passLbl = new JLabel("Password:");
        userLbl.setForeground(TEXT_COLOR);
        passLbl.setForeground(TEXT_COLOR);

        styleField(userField);
        styleField(passField);

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setOpaque(false);
        form.add(userLbl);
        form.add(userField);
        form.add(passLbl);
        form.add(passField);

        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn);
        loginBtn.addActionListener(e -> attemptLogin());

        status.setForeground(Color.RED);
        status.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);
        box.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        box.add(title);
        box.add(Box.createVerticalStrut(16));
        box.add(form);
        box.add(Box.createVerticalStrut(12));
        box.add(loginBtn);
        box.add(Box.createVerticalStrut(8));
        box.add(status);

        add(box, BorderLayout.CENTER);
    }

    private void styleField(JTextField field) {
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleButton(JButton btn) {
        btn.setBackground(BTN_BG);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void attemptLogin() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());

        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, role FROM users WHERE username=? AND password=?")) {
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String role = rs.getString("role");
                User logged = role.equals("admin") ? new Admin(id, user)
                                                   : new Customer(id, user);
                logged.createDashboard().setVisible(true);
                dispose();
            } else {
                status.setText("Invalid username or password.");
            }
        } catch (SQLException ex) {
            status.setText("Database error.");
            ex.printStackTrace();
        }
    }
}
