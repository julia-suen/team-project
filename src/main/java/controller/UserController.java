package controller;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import entities.User;
import view.LoginView;
import view.MainFrame;

public class UserController {

    private final MainFrame mainFrame;
    private User currentUser;

    public UserController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        requestLogin();
    }

    public UserController(MainFrame mainFrame, boolean showLoginOnStart) {
        this.mainFrame = mainFrame;
        if (showLoginOnStart) {
            requestLogin();
        }
    }

    /**
     * Gets currently logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Creates and displays the modal login dialog.
     */
    public void requestLogin() {
        final LoginView loginDialog = new LoginView(mainFrame);

        loginDialog.getLoginButton().addActionListener(event -> {
            final String username = loginDialog.getUsername();

            if (username != null && !username.trim().isEmpty()) {
                this.currentUser = new User(username);
                mainFrame.setTitle("Logged in as: " + this.currentUser.getUsername());

                loginDialog.dispose();
            }
            else {
                showErrorMessage(loginDialog, "Username cannot be empty.");
            }
        });

        loginDialog.setVisible(true);
    }

    public void logout() {
        if (currentUser != null) {
            this.currentUser = null;
            mainFrame.setTitle("Canada Wildfire Data Viewer"); // Reset title

            JOptionPane.showMessageDialog(
                    mainFrame,
                    "You have been logged out successfully.",
                    "Logged Out",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void showErrorMessage(JDialog owner, String message) {
        JOptionPane.showMessageDialog(owner, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }
}
