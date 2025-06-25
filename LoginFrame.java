import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private final JTextField userField = new JTextField(12);
    private final JPasswordField passField = new JPasswordField(12);
    private final JLabel status = new JLabel(" ");

    public LoginFrame() {
        super("Hotel Booking – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(340, 180);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        // === Form Panel ===
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");

        
        userLabel.setForeground(Color.WHITE);
        passLabel.setForeground(Color.WHITE);

        form.setBackground(new Color(30, 30, 30));
        form.add(userLabel);
        form.add(userField);
        form.add(passLabel);
        form.add(passField);

        
        Color fieldBg = new Color(50, 50, 50);
        Color border = new Color(80, 80, 80);
        userField.setBackground(fieldBg);
        userField.setForeground(Color.WHITE);
        userField.setCaretColor(Color.WHITE);
        userField.setBorder(BorderFactory.createLineBorder(border));

        passField.setBackground(fieldBg);
        passField.setForeground(Color.WHITE);
        passField.setCaretColor(Color.WHITE);
        passField.setBorder(BorderFactory.createLineBorder(border));

        
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(70, 70, 70));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        loginBtn.addActionListener(e -> attemptLogin());

        
        status.setForeground(Color.RED);
        status.setHorizontalAlignment(JLabel.CENTER);

        
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(30, 30, 30));
        root.add(status, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(loginBtn, BorderLayout.SOUTH);

        setContentPane(root);
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
                status.setText("Invalid credentials");
            }
        } catch (SQLException ex) {
            status.setText("DB error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
