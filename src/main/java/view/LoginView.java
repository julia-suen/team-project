package view;

import java.awt.*;

import javax.swing.*;

public class LoginView extends JDialog {
    private static final int BORDER_DIM = 10;
    private static final int TEXT_FIELD_LEN = 15;
    private final JTextField usernameField = new JTextField(TEXT_FIELD_LEN);
    private final JPasswordField passwordField = new JPasswordField(TEXT_FIELD_LEN);
    private final JButton loginButton = new JButton("Login");

    public LoginView(JFrame parent) {
        super(parent, "Login", true);

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_DIM, BORDER_DIM, BORDER_DIM, BORDER_DIM));

        // Username row
        final JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.add(new JLabel("Username:"));
        userPanel.add(usernameField);

        // Password row
        final JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passPanel.add(new JLabel("Password:"));
        passPanel.add(passwordField);

        mainPanel.add(userPanel);
        mainPanel.add(passPanel);
        // Vertical space
        mainPanel.add(Box.createRigidArea(new Dimension(0, BORDER_DIM)));
        mainPanel.add(loginButton);

        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(mainPanel);

        pack();
        setLocationRelativeTo(parent);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public JButton getLoginButton() {
        return loginButton;
    }
}
