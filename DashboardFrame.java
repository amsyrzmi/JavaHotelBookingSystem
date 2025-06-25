// ==============================
// DashboardFrame.java
// ==============================
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class DashboardFrame extends JFrame {

    private final User user;

    //! Widgets
    private final JTable roomTable    = new JTable();
    private final JTable bookingTable = new JTable();
    private final JTable userTable    = new JTable();       

    private final JButton refreshBtn   = new JButton("Refresh");
    private final JButton bookBtn      = new JButton("Book");
    private final JButton cancelBtn    = new JButton("Cancel Booking");
    private final JButton deleteUserBtn= new JButton("Delete User");
    private final JButton logoutBtn    = new JButton("Logout");   

    //!Darkmode pallete
    private static final Color DARK_BG    = new Color(30, 30, 30);
    private static final Color MID_BG     = new Color(40, 40, 40);
    private static final Color BTN_BG     = new Color(70, 70, 70);
    private static final Color TEXT_COL   = Color.WHITE;
    private static final Color BORDER_COL = new Color(60, 60, 60);

    
    public DashboardFrame(User u) {
        super("Dashboard – " + u.getUsername());
        this.user = u;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);   
        buildUI();
        loadData();
    }

    //!UI
    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(DARK_BG);

        //! Left Alligned Items
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        left.setBackground(DARK_BG);

        for (JButton b : new JButton[]{ refreshBtn, bookBtn, cancelBtn, deleteUserBtn })
            styleBtn(b);

        left.add(refreshBtn);
        if (user instanceof Customer) left.add(bookBtn);
        left.add(cancelBtn);
        if (user instanceof Admin)    left.add(deleteUserBtn);

        //! Right Alligned Items
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        right.setBackground(DARK_BG);
        styleBtn(logoutBtn);
        right.add(logoutBtn);

        top.add(left , BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        //! Tables
        int columns = (user instanceof Admin) ? 3 : 2;
        JPanel center = new JPanel(new GridLayout(1, columns, 8, 8));
        center.setBackground(MID_BG);
        center.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

        JScrollPane spRooms    = makeScroll(roomTable   , "Room Availability");
        JScrollPane spBookings = makeScroll(bookingTable, "Booked Rooms");
        JScrollPane spUsers    = makeScroll(userTable   , "Manage Users");

        center.add(spRooms);
        center.add(spBookings);
        if (user instanceof Admin) center.add(spUsers);

        //! 
        refreshBtn   .addActionListener(e -> loadData());
        bookBtn      .addActionListener(e -> handleBook());
        cancelBtn    .addActionListener(e -> handleCancel());
        deleteUserBtn.addActionListener(e -> handleDeleteUser());
        logoutBtn    .addActionListener(e -> {
            dispose();               
            new LoginFrame().setVisible(true);  
        });

        /* ---- add to frame ---- */
        add(top   , BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private void styleBtn(JButton b) {
        b.setBackground(BTN_BG);
        b.setForeground(TEXT_COL);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
    }

    private JScrollPane makeScroll(JTable table, String title) {
            styleTable(table);

            JScrollPane sp = new JScrollPane(table);
            sp.getViewport().setBackground(DARK_BG);
            sp.setBorder(new TitledBorder(
                    BorderFactory.createLineBorder(BORDER_COL),
                    title,
                    TitledBorder.LEADING,
                    TitledBorder.TOP,
                    sp.getFont(),
                    Color.BLACK));
            return sp;
        }

        private void styleTable(JTable table) {
            table.setFillsViewportHeight(true);
            table.setGridColor(DARK_BG); 
            table.setRowHeight(28);
            table.setFont(new Font("SansSerif", Font.PLAIN, 13));
            table.setForeground(Color.WHITE);
            table.setBackground(new Color(45, 45, 45));
            table.setSelectionBackground(new Color(70, 130, 180));
            table.setSelectionForeground(Color.WHITE);

            JTableHeader header = table.getTableHeader();
            header.setFont(new Font("SansSerif", Font.BOLD, 14));
            header.setBackground(new Color(30, 30, 30));
            header.setForeground(Color.WHITE);
            header.setReorderingAllowed(false);
        }

    //! Database Operations
    private void loadData() {
        try (Connection c = DatabaseConnector.getConnection()) {
            /* Rooms */
            String roomSql = (user instanceof Admin)
                    ? "SELECT * FROM rooms"
                    : "SELECT * FROM rooms WHERE available=TRUE";
            roomTable.setModel(buildModel(c.createStatement().executeQuery(roomSql)));

            /* Bookings */
            PreparedStatement ps;
            if (user instanceof Admin) {
                ps = c.prepareStatement(
                        "SELECT b.id, u.username, r.type AS room, b.date, b.status " +
                        "FROM bookings b " +
                        "JOIN users u ON b.user_id=u.id " +
                        "JOIN rooms r ON b.room_id=r.id");
            } else {
                ps = c.prepareStatement(
                        "SELECT b.id, r.type AS room, b.date, b.status " +
                        "FROM bookings b " +
                        "JOIN rooms r ON b.room_id=r.id " +
                        "WHERE b.user_id=?");
                ps.setInt(1, user.id);
            }
            bookingTable.setModel(buildModel(ps.executeQuery()));

            /* Users */
            if (user instanceof Admin) {
                ResultSet rsUsers = c.createStatement()
                                     .executeQuery("SELECT id, username, password, role FROM users");
                userTable.setModel(buildModel(rsUsers));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static DefaultTableModel buildModel(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        Vector<String> cols = new Vector<>();
        for (int i = 1; i <= md.getColumnCount(); i++) cols.add(md.getColumnName(i));

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= md.getColumnCount(); i++)
                row.add(rs.getObject(i));
            data.add(row);
        }
        return new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    //! Functionality
    private void handleBook() {
        if (!(user instanceof Customer)) return;
        int row = roomTable.getSelectedRow();
        if (row < 0) return;

        int roomId = (int) roomTable.getValueAt(row, 0);

        try (Connection c = DatabaseConnector.getConnection()) {
            c.setAutoCommit(false);

            PreparedStatement ins = c.prepareStatement(
                    "INSERT INTO bookings(user_id, room_id, date, status) " +
                    "VALUES (?, ?, CURDATE(), 'Booked')");
            ins.setInt(1, user.id);
            ins.setInt(2, roomId);
            ins.executeUpdate();

            PreparedStatement upd = c.prepareStatement(
                    "UPDATE rooms SET available=FALSE WHERE id=?");
            upd.setInt(1, roomId);
            upd.executeUpdate();

            c.commit();
            loadData();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void handleCancel() {
        int row = bookingTable.getSelectedRow();
        if (row < 0) return;

        int bookingId = (int) bookingTable.getValueAt(row, 0);

        try (Connection c = DatabaseConnector.getConnection()) {
            PreparedStatement sel = c.prepareStatement(
                    "SELECT room_id, user_id FROM bookings WHERE id=?");
            sel.setInt(1, bookingId);
            ResultSet rs = sel.executeQuery();
            if (!rs.next()) return;

            int roomId = rs.getInt("room_id");
            int owner  = rs.getInt("user_id");

            if (user instanceof Customer && owner != user.id) return;

            c.setAutoCommit(false);

            PreparedStatement del = c.prepareStatement(
                    "DELETE FROM bookings WHERE id=?");
            del.setInt(1, bookingId);
            del.executeUpdate();

            PreparedStatement upd = c.prepareStatement(
                    "UPDATE rooms SET available=TRUE WHERE id=?");
            upd.setInt(1, roomId);
            upd.executeUpdate();

            c.commit();
            loadData();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void handleDeleteUser() {
        if (!(user instanceof Admin)) return;
        int row = userTable.getSelectedRow();
        if (row < 0) return;

        int uid = (int) userTable.getValueAt(row, 0);
        if (uid == user.id) {
            JOptionPane.showMessageDialog(this, "You cannot delete yourself.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                this, "Delete selected user and their bookings?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try (Connection c = DatabaseConnector.getConnection()) {
            c.setAutoCommit(false);

            //! Free Rooms
            PreparedStatement free = c.prepareStatement(
                    "UPDATE rooms r JOIN bookings b ON r.id=b.room_id " +
                    "SET r.available=TRUE WHERE b.user_id=?");
            free.setInt(1, uid);
            free.executeUpdate();

            //! Bookings Deletion
            PreparedStatement delB = c.prepareStatement(
                    "DELETE FROM bookings WHERE user_id=?");
            delB.setInt(1, uid);
            delB.executeUpdate();

            //! User Deletion
            PreparedStatement delU = c.prepareStatement(
                    "DELETE FROM users WHERE id=?");
            delU.setInt(1, uid);
            delU.executeUpdate();

            c.commit();
            loadData();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
