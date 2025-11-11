
package com.sekhmet.ui.controllers;
import com.sekhmet.bootstrap.DatabaseBootstrap;
import com.sekhmet.service.AuthService;
import com.sekhmet.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    private AuthService authService = new AuthService();
    @FXML public void initialize() { DatabaseBootstrap.ensureDefaultAdmin(); }
    @FXML public void onLogin() {
        String u = usernameField.getText().trim(); String p = passwordField.getText();
        User user = authService.authenticate(u, p);
        if (user != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
                Scene scene = new Scene(loader.load());
                Stage s = (Stage) usernameField.getScene().getWindow();
                s.setScene(scene);
                com.sekhmet.ui.controllers.MainController ctrl = loader.getController();
                ctrl.setCurrentUser(user);
            } catch (IOException ex) { ex.printStackTrace(); messageLabel.setText("Could not open main window."); }
        } else { messageLabel.setText("Invalid credentials."); }
    }
    @FXML public void onForgot() { messageLabel.setText("Use admin account (demo)."); }
}
