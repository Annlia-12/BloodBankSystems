package bloodbank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddRecipientScreen extends JFrame implements ActionListener {
    private JTextField nameField, ageField, contactField, reasonField;
    private JComboBox<String> bloodGroupCombo;
    private JButton submitButton, cancelButton;
    private Connection con;
    private int hospitalId;
    private String hospitalName;

    private final String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    public AddRecipientScreen(Connection con, int hospitalId, String hospitalName) {
        this.con = con;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;

        setTitle("Register Recipient - " + hospitalName);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(153, 0, 0));
        JLabel title = new JLabel("Register Patient/Recipient");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        form.add(new JLabel("Patient Name:"));
        nameField = new JTextField();
        form.add(nameField);

        form.add(new JLabel("Blood Group:"));
        bloodGroupCombo = new JComboBox<>(bloodGroups);
        form.add(bloodGroupCombo);

        form.add(new JLabel("Age:"));
        ageField = new JTextField();
        form.add(ageField);

        form.add(new JLabel("Contact:"));
        contactField = new JTextField();
        form.add(contactField);

        form.add(new JLabel("Reason/Condition:"));
        reasonField = new JTextField();
        form.add(reasonField);

        form.add(new JLabel(""));
        form.add(new JLabel(""));

        submitButton = new JButton("Register Patient");
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String name = nameField.getText().trim();
            String bloodGroup = bloodGroupCombo.getSelectedItem().toString();
            String contact = contactField.getText().trim();
            String reason = reasonField.getText().trim();

            int age;
            try {
                age = Integer.parseInt(ageField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid age.");
                return;
            }

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Patient name is required.");
                return;
            }

            try {
                String query = "INSERT INTO recipients(hospital_id, name, blood_group, age, contact, reason) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, hospitalId);
                ps.setString(2, name);
                ps.setString(3, bloodGroup);
                ps.setInt(4, age);
                ps.setString(5, contact);
                ps.setString(6, reason);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Patient/Recipient registered successfully!");
                dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}
