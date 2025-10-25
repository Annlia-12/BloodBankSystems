package bloodbank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HospitalLoginScreen extends JFrame implements ActionListener {
    private JComboBox<String> hospitalCombo;
    private JPasswordField passField;
    private JButton loginButton, signupButton, resetButton;
    private JLabel msgLabel;
    private Connection con;

    public HospitalLoginScreen() {
        con = BloodBankDB.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Cannot connect to database.");
            System.exit(0);
        }

        setTitle("Kerala Blood Bank - Hospital Login");
        setSize(450, 350);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(153, 0, 0));
        JLabel title = new JLabel("Hospital Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        form.add(new JLabel("Hospital:"));
        hospitalCombo = new JComboBox<>();
        loadHospitals();
        form.add(hospitalCombo);

        form.add(new JLabel("Password:"));
        passField = new JPasswordField();
        form.add(passField);

        msgLabel = new JLabel("", SwingConstants.CENTER);
        msgLabel.setForeground(Color.RED);
        add(msgLabel, BorderLayout.SOUTH);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 128, 0));
        loginButton.setForeground(Color.WHITE);
        resetButton = new JButton("Reset");
        signupButton = new JButton("New Hospital?");
        signupButton.setForeground(new Color(153, 0, 0));

        buttonPanel.add(loginButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(signupButton);

        add(form, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(this);
        resetButton.addActionListener(e -> passField.setText(""));
        signupButton.addActionListener(e -> {
            new HospitalSignUpScreen(con, this);
        });

        setVisible(true);
    }

    public void loadHospitals() {
        hospitalCombo.removeAllItems();
        try {
            String query = "SELECT hospital_name FROM hospitals ORDER BY hospital_name ASC";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) hospitalCombo.addItem(rs.getString("hospital_name"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading hospitals: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String hospital = (String) hospitalCombo.getSelectedItem();
        String password = new String(passField.getPassword());

        try {
            String sql = "SELECT id, district FROM hospitals WHERE hospital_name=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, hospital);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String district = rs.getString("district");
                JOptionPane.showMessageDialog(this, "Welcome " + hospital + "!");
                dispose();
                new HospitalMainMenu(con, id, hospital, district);
            } else msgLabel.setText("Invalid password!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HospitalLoginScreen::new);
    }
}
