package controller;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import entities.User;
import use_case.favourites.AddFavouriteInteractor;
import view.LoginView;
import view.MainFrame;

public class UserController {

    private final MainFrame mainFrame;
    private final AddFavouriteInteractor favouritesInteractor;
    private User currentUser;

    public UserController(MainFrame mainFrame, AddFavouriteInteractor favouritesInteractor) {
        this.mainFrame = mainFrame;
        this.favouritesInteractor = favouritesInteractor;
        requestLogin();
    }

    /**
     * Creates and displays the model login dialog.
     */
    public void requestLogin() {
        final LoginView loginDialog = new LoginView(mainFrame);

        loginDialog.getLoginButton().addActionListener(event -> {
            final String username = loginDialog.getUsername();

            if (username != null && !username.trim().isEmpty()) {
                this.currentUser = new User(username);
                mainFrame.setTitle("Logged in as: " + this.currentUser.getUsername());

                // Set user in favourites interactor
                favouritesInteractor.setCurrentUser(this.currentUser);

                loginDialog.dispose();
            }
            else {
                showErrorMessage(loginDialog, "Username cannot be empty.");
            }
        });

        loginDialog.setVisible(true);
    }

    private void showErrorMessage(JDialog owner, String message) {
        JOptionPane.showMessageDialog(owner, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
