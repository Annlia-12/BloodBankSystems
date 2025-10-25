package bloodbank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewStockScreen extends JFrame {
    private JTable table;
    private Connection con;
    private int hospitalId;
    private String hospitalName;

    private final String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    public ViewStockScreen(Connection con, int hospitalId, String hospitalName) {
        this.con = con;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;

        setTitle("Blood Stock - " + hospitalName);
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(153, 0, 0));
        JLabel heading = new JLabel("📊 Current Blood Stock - " + hospitalName, JLabel.CENTER);
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(heading);
        add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"Blood Group", "Units Available", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadBloodStock(model);

        setVisible(true);
    }

    private void loadBloodStock(DefaultTableModel model) {
        try {
            String query = "SELECT blood_group, units FROM blood_stock WHERE hospital_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, hospitalId);
            ResultSet rs = ps.executeQuery();

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                int units = rs.getInt("units");
                String status = units > 10 ? "Good" : units > 5 ? "Low" : "Critical";
                model.addRow(new Object[]{
                        rs.getString("blood_group"),
                        units,
                        status
                });
            }

            // If no data exists, insert default blood stock for this hospital
            if (!hasData) {
                PreparedStatement psInsert = con.prepareStatement(
                        "INSERT INTO blood_stock(hospital_id, blood_group, units) VALUES(?, ?, ?)"
                );
                for (String bloodGroup : bloodGroups) {
                    psInsert.setInt(1, hospitalId);
                    psInsert.setString(2, bloodGroup);
                    psInsert.setInt(3, 0);
                    psInsert.executeUpdate();
                    model.addRow(new Object[]{bloodGroup, 0, "❌ Critical"});
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading blood stock: " + ex.getMessage());
        }
    }
}
