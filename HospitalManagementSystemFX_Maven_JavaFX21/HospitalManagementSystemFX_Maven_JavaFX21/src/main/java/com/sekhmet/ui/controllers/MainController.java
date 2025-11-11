
package com.sekhmet.ui.controllers;
import com.sekhmet.model.User;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
public class MainController {
    @FXML private StackPane pages;
    private User currentUser;
    public void setCurrentUser(User user) { this.currentUser = user; showMedicalRecords(); }
    private void loadPage(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxml));
            Parent p = loader.load();
            pages.getChildren().clear();
            pages.getChildren().add(p);
            if (fxml.equals("medical_records.fxml")) {
                MedicalRecordsController ctrl = loader.getController();
                ctrl.initData(currentUser);
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }
    @FXML public void showDashboard() { loadPage("medical_records.fxml"); }
    @FXML public void showPatientManagement() { loadPage("medical_records.fxml"); }
    @FXML public void showAppointment() { loadPage("medical_records.fxml"); }
    @FXML public void showMedicalRecords() { loadPage("medical_records.fxml"); }
    @FXML public void showBilling() { loadPage("medical_records.fxml"); }
    @FXML public void showHelp() { loadPage("medical_records.fxml"); }
    @FXML public void showMaintenance() { loadPage("medical_records.fxml"); }
}
