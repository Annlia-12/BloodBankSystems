package bloodbank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class NotificationsScreen extends JFrame {
    private JTable table;
    private Connection con;
    private int hospitalId;
    private String hospitalName;

    public NotificationsScreen(Connection con, int hospitalId, String hospitalName) {
        this.con = con;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;

        setTitle("Notifications - " + hospitalName);
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(153, 0, 0));
        JLabel heading = new JLabel("Your Notifications", JLabel.CENTER);
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(heading);
        add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Type", "Message", "Status", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadNotifications(model);

        setVisible(true);
    }

    private void loadNotifications(DefaultTableModel model) {
        try {
            String query = "SELECT id, type, message, status, created_at FROM notifications WHERE hospital_id = ? ORDER BY created_at DESC";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, hospitalId);
            ResultSet rs = ps.executeQuery();

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getString("message"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                });
            }

            if (!hasData) {
                JOptionPane.showMessageDialog(this, "No notifications yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

            // Mark all as read
            String markRead = "UPDATE notifications SET status = 'Read' WHERE hospital_id = ? AND status = 'Unread'";
            PreparedStatement psUpdate = con.prepareStatement(markRead);
            psUpdate.setInt(1, hospitalId);
            psUpdate.executeUpdate();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading notifications: " + ex.getMessage());
        }
    }
}
