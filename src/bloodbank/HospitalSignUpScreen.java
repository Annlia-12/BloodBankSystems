package bloodbank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HospitalSignUpScreen extends JFrame implements ActionListener {
    private JTextField nameField, districtField, locationField, contactField, emailField;
    private JPasswordField passField, confirmField;
    private JButton registerButton, cancelButton;
    private Connection con;
    private HospitalLoginScreen loginScreen; // reference to reload combo

    private final String[] bloodGroups = {"A+","A-","B+","B-","AB+","AB-","O+","O-"};

    public HospitalSignUpScreen(Connection con, HospitalLoginScreen loginScreen) {
        this.con = con;
        this.loginScreen = loginScreen;

        setSize(500, 550);
        setTitle("Register New Hospital");
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel header = new JPanel();
        header.setBackground(new Color(153, 0, 0));
        JLabel title = new JLabel("New Hospital Registration", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(8, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        form.add(new JLabel("Hospital Name:"));
        nameField = new JTextField(); form.add(nameField);

        form.add(new JLabel("District:"));
        districtField = new JTextField(); form.add(districtField);

        form.add(new JLabel("Location:"));
        locationField = new JTextField(); form.add(locationField);

        form.add(new JLabel("Contact:"));
        contactField = new JTextField(); form.add(contactField);

        form.add(new JLabel("Email:"));
        emailField = new JTextField(); form.add(emailField);

        form.add(new JLabel("Password:"));
        passField = new JPasswordField(); form.add(passField);

        form.add(new JLabel("Confirm Password:"));
        confirmField = new JPasswordField(); form.add(confirmField);

        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(0, 128, 0));
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.GRAY);
        cancelButton.addActionListener(e -> dispose());

        form.add(registerButton);
        form.add(cancelButton);
        add(form, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String hospital = nameField.getText().trim();
        String district = districtField.getText().trim();
        String location = locationField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (hospital.isEmpty() || district.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill required fields.");
            return;
        }
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }

        try {
            // Check duplicates
            String check = "SELECT hospital_name FROM hospitals WHERE hospital_name=?";
            PreparedStatement psC = con.prepareStatement(check);
            psC.setString(1, hospital);
            ResultSet rs = psC.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Hospital already exists!");
                return;
            }

            // Insert new hospital
            String insert = "INSERT INTO hospitals(hospital_name,district,location,contact,email,password) VALUES(?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, hospital);
            ps.setString(2, district);
            ps.setString(3, location);
            ps.setString(4, contact);
            ps.setString(5, email);
            ps.setString(6, pass);
            ps.executeUpdate();

            ResultSet genKey = ps.getGeneratedKeys();
            if (genKey.next()) {
                int hospitalId = genKey.getInt(1);

                // Create initial stock records
                for (String bg : bloodGroups) {
                    String stockSQL = "INSERT INTO blood_stock(hospital_id,blood_group,units) VALUES(?,?,0)";
                    PreparedStatement psS = con.prepareStatement(stockSQL);
                    psS.setInt(1, hospitalId);
                    psS.setString(2, bg);
                    psS.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "✅ Hospital Registered Successfully!");
            dispose();
            loginScreen.loadHospitals();  // reload combo live

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
