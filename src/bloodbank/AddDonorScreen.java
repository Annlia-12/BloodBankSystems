package bloodbank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddDonorScreen extends JFrame implements ActionListener {
    private JTextField nameField, ageField, contactField, addressField, healthField;
    private JComboBox<String> bloodGroupCombo, genderCombo;
    private JButton submitButton, cancelButton;
    private Connection con;
    private int hospitalId;
    private String hospitalName;

    private final String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private final String[] genders = {"Male", "Female", "Other"};

    public AddDonorScreen(Connection con, int hospitalId, String hospitalName) {
        this.con = con;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;

        setTitle("Register Donor - " + hospitalName);
        setSize(500, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(153, 0, 0));
        JLabel title = new JLabel(" Register New Donor");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridLayout(9, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        form.setBackground(Color.WHITE);

        form.add(new JLabel("Donor Name:"));
        nameField = new JTextField();
        form.add(nameField);

        form.add(new JLabel("Blood Group:"));
        bloodGroupCombo = new JComboBox<>(bloodGroups);
        form.add(bloodGroupCombo);

        form.add(new JLabel("Age:"));
        ageField = new JTextField();
        form.add(ageField);

        form.add(new JLabel("Gender:"));
        genderCombo = new JComboBox<>(genders);
        form.add(genderCombo);

        form.add(new JLabel("Contact Number:"));
        contactField = new JTextField();
        form.add(contactField);

        form.add(new JLabel("Address:"));
        addressField = new JTextField();
        form.add(addressField);

        form.add(new JLabel("Health Issues (if any):"));
        healthField = new JTextField();
        form.add(healthField);

        form.add(new JLabel(""));
        form.add(new JLabel(""));

        submitButton = new JButton("Register Donor");
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
            String gender = genderCombo.getSelectedItem().toString();
            String contact = contactField.getText().trim();
            String address = addressField.getText().trim();
            String healthIssues = healthField.getText().trim();

            int age;
            try {
                age = Integer.parseInt(ageField.getText().trim());
                if (age < 18 || age > 65) {
                    JOptionPane.showMessageDialog(this, "Age must be between 18 and 65.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid age.");
                return;
            }

            if (name.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Contact are required.");
                return;
            }

            try {
                String query = "INSERT INTO donors(hospital_id, name, blood_group, age, gender, contact, address, health_issues) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, hospitalId);
                ps.setString(2, name);
                ps.setString(3, bloodGroup);
                ps.setInt(4, age);
                ps.setString(5, gender);
                ps.setString(6, contact);
                ps.setString(7, address);
                ps.setString(8, healthIssues);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Donor registered successfully!");
                dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}
