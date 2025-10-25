package bloodbank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DonationHistoryScreen extends JFrame {
    private JTable table;
    private Connection con;
    private int hospitalId;
    private String hospitalName;

    public DonationHistoryScreen(Connection con, int hospitalId, String hospitalName) {
        this.con = con;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;

        setTitle("Donation History - " + hospitalName);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(153, 0, 0));
        JLabel heading = new JLabel("Donation History", JLabel.CENTER);
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(heading);
        add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Donor Name", "Blood Group", "Units", "Donation Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadHistory(model);

        setVisible(true);
    }

    private void loadHistory(DefaultTableModel model) {
        try {
            String query = "SELECT id, donor_name, blood_group, units, donation_date FROM donation_history WHERE hospital_id = ? ORDER BY donation_date DESC";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, hospitalId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("donor_name"),
                        rs.getString("blood_group"),
                        rs.getInt("units"),
                        rs.getTimestamp("donation_date")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No donation history found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading history: " + ex.getMessage());
        }
    }
}
