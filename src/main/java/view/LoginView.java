package view;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JDialog {
  private final JTextField usernameField = new JTextField(15);
  private final JPasswordField passwordField = new JPasswordField(15);
  private final JButton loginButton = new JButton("Login");

  public LoginView(JFrame parent) {
    super(parent, "Login", true);

    setLayout(new FlowLayout());
    add(new JLabel("Username:"));
    add(usernameField);
    add(new JLabel("Password:"));
    add(passwordField);
    add(loginButton);

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
