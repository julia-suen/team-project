package controller;

import model.User;
import view.LoginView;
import view.MainFrame;
import javax.swing.JOptionPane;
import javax.swing.JDialog;

public class UserController {

  private final MainFrame mainFrame;
  private User currentUser;

  public UserController(MainFrame mainFrame) {
    this.mainFrame = mainFrame;
    requestLogin();
  }

  /**
   * Creates and displays the modal login dialog.
   */
  public void requestLogin() {
    LoginView loginDialog = new LoginView(mainFrame);

    loginDialog.getLoginButton().addActionListener(e -> {
      String username = loginDialog.getUsername();

      if (username != null && !username.trim().isEmpty()) {
        this.currentUser = new User(username);
        mainFrame.setTitle("Logged in as: " + this.currentUser.getUsername());

        loginDialog.dispose();
      } else {
        showErrorMessage(loginDialog, "Username cannot be empty.");
      }
    });

    loginDialog.setVisible(true);
  }

  private void showErrorMessage(JDialog owner, String message) {
    JOptionPane.showMessageDialog(owner, message, "Login Error", JOptionPane.ERROR_MESSAGE);
  }
}
