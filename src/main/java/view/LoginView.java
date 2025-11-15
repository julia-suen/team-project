package view;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JDialog {
  private final JTextField usernameField = new JTextField(15);
  private final JPasswordField passwordField = new JPasswordField(15);
  private final JButton loginButton = new JButton("Login");

  public LoginView(JFrame parent) {
    super(parent, "Login", true);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Username row
    JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    userPanel.add(new JLabel("Username:"));
    userPanel.add(usernameField);

    // Password row
    JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    passPanel.add(new JLabel("Password:"));
    passPanel.add(passwordField);

    mainPanel.add(userPanel);
    mainPanel.add(passPanel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Vertical space
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
