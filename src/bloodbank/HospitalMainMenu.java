package bloodbank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;

public class HospitalMainMenu extends JFrame implements ActionListener {
    JButton addDonor, viewStock, addRecipient, requestBlood, donorList, donationHistory, bloodCamps, notifications, exitButton;
    Connection con;
    int hospitalId;
    String hospitalName;
    String district;

    public HospitalMainMenu(Connection con, int hospitalId, String hospitalName, String district) {
        this.con = con;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.district = district;

        setTitle("Hospital Dashboard");
        setSize(600, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(153, 0, 0));
        header.setPreferredSize(new Dimension(600, 80));
        header.setLayout(new BorderLayout());
        
        JLabel title = new JLabel(" " + hospitalName, SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JLabel subtitle = new JLabel("District: " + district, SwingConstants.CENTER);
        subtitle.setForeground(Color.WHITE);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        header.add(title, BorderLayout.CENTER);
        header.add(subtitle, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Center - Buttons
        JPanel center = new JPanel(new GridLayout(9, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        addDonor = createMenuButton(" Register Donor");
        viewStock = createMenuButton(" View Blood Stock");
        addRecipient = createMenuButton(" Register Recipient");
        requestBlood = createMenuButton(" Request Blood");
        donorList = createMenuButton(" View All Donors");
        donationHistory = createMenuButton(" Donation History");
        bloodCamps = createMenuButton(" Blood Camps / Events");
        notifications = createMenuButton(" Notifications");
        exitButton = createMenuButton(" Logout");

        center.add(addDonor);
        center.add(viewStock);
        center.add(addRecipient);
        center.add(requestBlood);
        center.add(donorList);
        center.add(donationHistory);
        center.add(bloodCamps);
        center.add(notifications);
        center.add(exitButton);

        add(center, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(220, 0, 0));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.addActionListener(this);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addDonor) 
            new AddDonorScreen(con, hospitalId, hospitalName);
        else if (e.getSource() == viewStock) 
            new ViewStockScreen(con, hospitalId, hospitalName);
        else if (e.getSource() == addRecipient) 
            new AddRecipientScreen(con, hospitalId, hospitalName);
        else if (e.getSource() == requestBlood) 
            new RequestBloodScreen(con, hospitalId, hospitalName);
        else if (e.getSource() == donorList) 
            new DonorListScreen(con, hospitalId, hospitalName);
        else if (e.getSource() == donationHistory) 
            new DonationHistoryScreen(con, hospitalId, hospitalName);
        else if (e.getSource() == bloodCamps) 
            new BloodCampsScreen(con);
        else if (e.getSource() == notifications) 
            new NotificationsScreen(con, hospitalId, hospitalName);
        else if (e.getSource() == exitButton) {
            dispose();
            new HospitalLoginScreen();
        }
    }
}
