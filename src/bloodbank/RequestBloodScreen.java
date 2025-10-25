package bloodbank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RequestBloodScreen extends JFrame implements ActionListener {
    private JComboBox<String> recipientCombo, bloodGroupCombo;
    private JTextField unitsField;
    private JButton submitButton, cancelButton, refreshButton;
    private Connection con;
    private int hospitalId;
    private String hospitalName;

    private final String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    public RequestBloodScreen(Connection con, int hospitalId, String hospitalName) {
        this.con = con;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;

        setTitle("Request Blood - " + hospitalName);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(153, 0, 0));
        JLabel title = new JLabel("Request Blood");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        form.add(new JLabel("Select Patient:"));
        JPanel recipientPanel = new JPanel(new BorderLayout(5, 0));
        recipientCombo = new JComboBox<>();
        refreshButton = new JButton("⟳");
        refreshButton.setPreferredSize(new Dimension(45, 25));
        refreshButton.addActionListener(e -> loadRecipients());
        recipientPanel.add(recipientCombo, BorderLayout.CENTER);
        recipientPanel.add(refreshButton, BorderLayout.EAST);
        form.add(recipientPanel);
        loadRecipients();

        form.add(new JLabel("Blood Group:"));
        bloodGroupCombo = new JComboBox<>(bloodGroups);
        form.add(bloodGroupCombo);

        form.add(new JLabel("Units Required:"));
        unitsField = new JTextField();
        form.add(unitsField);

        form.add(new JLabel(""));
        form.add(new JLabel(""));

        submitButton = new JButton("Submit Request");
        submitButton.setBackground(new Color(153, 0, 0));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        submitButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.GRAY);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> dispose());

        form.add(submitButton);
        form.add(cancelButton);

        add(form, BorderLayout.CENTER);
        setVisible(true);
    }

    private void loadRecipients() {
        recipientCombo.removeAllItems();
        try {
            String query = "SELECT id, name, blood_group FROM recipients WHERE hospital_id = ? ORDER BY name";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, hospitalId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                recipientCombo.addItem(rs.getInt("id") + " - " + rs.getString("name") + " (" + rs.getString("blood_group") + ")");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading recipients: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String recipientData = (String) recipientCombo.getSelectedItem();
            if (recipientData == null) {
                JOptionPane.showMessageDialog(this, "No recipient selected. Please register a patient first.");
                return;
            }

            int recipientId = Integer.parseInt(recipientData.split(" - ")[0]);
            String bloodGroup = bloodGroupCombo.getSelectedItem().toString();
            int units;

            try {
                units = Integer.parseInt(unitsField.getText().trim());
                if (units <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid number of units.");
                return;
            }

            try {
                // Get recipient name
                String getRecipient = "SELECT name FROM recipients WHERE id = ?";
                PreparedStatement psName = con.prepareStatement(getRecipient);
                psName.setInt(1, recipientId);
                ResultSet rsName = psName.executeQuery();
                String recipientName = "";
                if (rsName.next()) recipientName = rsName.getString("name");

                // Check stock
                String checkStock = "SELECT units FROM blood_stock WHERE hospital_id = ? AND blood_group = ?";
                PreparedStatement psCheck = con.prepareStatement(checkStock);
                psCheck.setInt(1, hospitalId);
                psCheck.setString(2, bloodGroup);
                ResultSet rs = psCheck.executeQuery();

                int availableUnits = 0;
                if (rs.next()) availableUnits = rs.getInt("units");

                String status;
                if (availableUnits >= units) {
                    // Approve and deduct stock
                    String updateStock = "UPDATE blood_stock SET units = units - ? WHERE hospital_id = ? AND blood_group = ?";
                    PreparedStatement psUpdate = con.prepareStatement(updateStock);
                    psUpdate.setInt(1, units);
                    psUpdate.setInt(2, hospitalId);
                    psUpdate.setString(3, bloodGroup);
                    psUpdate.executeUpdate();
                    status = "Approved";
                } else {
                    status = "Pending";
                }

                // Insert request
                String insertReq = "INSERT INTO blood_requests(hospital_id, recipient_id, recipient_name, blood_group, units, status) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement psReq = con.prepareStatement(insertReq);
                psReq.setInt(1, hospitalId);
                psReq.setInt(2, recipientId);
                psReq.setString(3, recipientName);
                psReq.setString(4, bloodGroup);
                psReq.setInt(5, units);
                psReq.setString(6, status);
                psReq.executeUpdate();

                // Add notification
                String notif = "INSERT INTO notifications(hospital_id, type, message) VALUES (?, ?, ?)";
                PreparedStatement psNotif = con.prepareStatement(notif);
                psNotif.setInt(1, hospitalId);
                psNotif.setString(2, "Blood Request " + status);
                psNotif.setString(3, "Request for " + units + " units of " + bloodGroup + " for patient " + recipientName + " - Status: " + status);
                psNotif.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Request submitted! Status: " + status);
                dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}
