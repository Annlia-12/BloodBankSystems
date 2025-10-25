package bloodbank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BloodCampsScreen extends JFrame {
    private JTable table;
    private Connection con;

    public BloodCampsScreen(Connection con) {
        this.con = con;

        setTitle("Blood Donation Camps in Kerala");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(153, 0, 0));
        JLabel heading = new JLabel("Upcoming Blood Donation Camps", JLabel.CENTER);
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(heading);
        add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"Event Name", "District", "Location", "Date", "Contact", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadCamps(model);

        setVisible(true);
    }

    private void loadCamps(DefaultTableModel model) {
        try {
            String query = "SELECT name, district, location, event_date, contact, description FROM blood_camps WHERE event_date >= CURDATE() ORDER BY event_date ASC";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getString("district"),
                        rs.getString("location"),
                        rs.getDate("event_date"),
                        rs.getString("contact"),
                        rs.getString("description")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No upcoming blood camps scheduled.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading camps: " + ex.getMessage());
        }
    }
}
