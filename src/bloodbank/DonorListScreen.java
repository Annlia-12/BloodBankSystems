package bloodbank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DonorListScreen extends JFrame {
    private JTable table;
    private Connection con;
    private int hospitalId;
    private String hospitalName;
    private JTextField searchField;
    private JComboBox<String> bloodGroupFilter;
    private DefaultTableModel model;

    public DonorListScreen(Connection con, int hospitalId, String hospitalName) {
        this.con = con;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;

        setTitle("Donor List - " + hospitalName);
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(153, 0, 0));
        JLabel heading = new JLabel("All Registered Donors", JLabel.CENTER);
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(heading);
        add(header, BorderLayout.NORTH);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.add(new JLabel("Search:"));
        searchField = new JTextField(15);
        filterPanel.add(searchField);

        filterPanel.add(new JLabel("Blood Group:"));
        bloodGroupFilter = new JComboBox<>(new String[]{"All", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        filterPanel.add(bloodGroupFilter);

        JButton searchBtn = new JButton("🔍 Search");
        searchBtn.setBackground(new Color(153, 0, 0));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(e -> loadDonors());
        filterPanel.add(searchBtn);

        JButton donateBtn = new JButton("➕ Record Donation");
        donateBtn.setBackground(new Color(0, 153, 0));
        donateBtn.setForeground(Color.WHITE);
        donateBtn.addActionListener(e -> recordDonation());
        filterPanel.add(donateBtn);

        add(filterPanel, BorderLayout.SOUTH);

        // Table
        String[] columns = {"ID", "Name", "Blood Group", "Age", "Gender", "Contact", "Health Issues", "Last Donation"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadDonors();

        setVisible(true);
    }

    private void loadDonors() {
        model.setRowCount(0);
        try {
            String query = "SELECT id, name, blood_group, age, gender, contact, health_issues, last_donation_date FROM donors WHERE hospital_id = ?";
            String searchText = searchField.getText().trim();
            String bloodGroup = (String) bloodGroupFilter.getSelectedItem();

            if (!searchText.isEmpty()) {
                query += " AND name LIKE ?";
            }
            if (!bloodGroup.equals("All")) {
                query += " AND blood_group = ?";
            }
            query += " ORDER BY name";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, hospitalId);

            int paramIndex = 2;
            if (!searchText.isEmpty()) {
                ps.setString(paramIndex++, "%" + searchText + "%");
            }
            if (!bloodGroup.equals("All")) {
                ps.setString(paramIndex, bloodGroup);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("blood_group"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("contact"),
                        rs.getString("health_issues"),
                        rs.getDate("last_donation_date")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading donors: " + ex.getMessage());
        }
    }

    private void recordDonation() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a donor from the list.");
            return;
        }

        int donorId = (int) table.getValueAt(selectedRow, 0);
        String donorName = (String) table.getValueAt(selectedRow, 1);
        String bloodGroup = (String) table.getValueAt(selectedRow, 2);

        String unitsStr = JOptionPane.showInputDialog(this, "Enter units donated by " + donorName + ":", "1");
        if (unitsStr == null || unitsStr.trim().isEmpty()) return;

        try {
            int units = Integer.parseInt(unitsStr.trim());
            if (units <= 0) {
                JOptionPane.showMessageDialog(this, "Units must be greater than 0.");
                return;
            }

            // Insert donation record
            String donationQuery = "INSERT INTO donation_history(hospital_id, donor_id, donor_name, blood_group, units) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psDonation = con.prepareStatement(donationQuery);
            psDonation.setInt(1, hospitalId);
            psDonation.setInt(2, donorId);
            psDonation.setString(3, donorName);
            psDonation.setString(4, bloodGroup);
            psDonation.setInt(5, units);
            psDonation.executeUpdate();

            // Update blood stock
            String stockCheck = "SELECT units FROM blood_stock WHERE hospital_id = ? AND blood_group = ?";
            PreparedStatement psCheck = con.prepareStatement(stockCheck);
            psCheck.setInt(1, hospitalId);
            psCheck.setString(2, bloodGroup);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                String updateStock = "UPDATE blood_stock SET units = units + ? WHERE hospital_id = ? AND blood_group = ?";
                PreparedStatement psUpdate = con.prepareStatement(updateStock);
                psUpdate.setInt(1, units);
                psUpdate.setInt(2, hospitalId);
                psUpdate.setString(3, bloodGroup);
                psUpdate.executeUpdate();
            } else {
                String insertStock = "INSERT INTO blood_stock(hospital_id, blood_group, units) VALUES(?, ?, ?)";
                PreparedStatement psInsert = con.prepareStatement(insertStock);
                psInsert.setInt(1, hospitalId);
                psInsert.setString(2, bloodGroup);
                psInsert.setInt(3, units);
                psInsert.executeUpdate();
            }

            // Update last donation date
            String updateDonor = "UPDATE donors SET last_donation_date = NOW() WHERE id = ?";
            PreparedStatement psUpdateDonor = con.prepareStatement(updateDonor);
            psUpdateDonor.setInt(1, donorId);
            psUpdateDonor.executeUpdate();

            // Add notification
            String notif = "INSERT INTO notifications(hospital_id, type, message) VALUES (?, ?, ?)";
            PreparedStatement psNotif = con.prepareStatement(notif);
            psNotif.setInt(1, hospitalId);
            psNotif.setString(2, "Donation Recorded");
            psNotif.setString(3, donorName + " donated " + units + " units of " + bloodGroup);
            psNotif.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Donation recorded successfully!");
            loadDonors();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
