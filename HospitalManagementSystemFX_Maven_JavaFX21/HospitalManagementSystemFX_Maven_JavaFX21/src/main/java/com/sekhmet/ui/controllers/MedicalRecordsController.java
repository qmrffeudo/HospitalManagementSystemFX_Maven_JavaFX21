
package com.sekhmet.ui.controllers;
import com.sekhmet.dao.PatientDao;
import com.sekhmet.model.Patient;
import com.sekhmet.model.User;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.util.*;
import javafx.collections.transformation.FilteredList;
public class MedicalRecordsController {
    @FXML private TableView<Patient> table;
    @FXML private TableColumn<Patient, String> caseNoCol, lastNameCol, firstNameCol, middleNameCol, dateAddedCol, statusCol;
    @FXML private TableColumn<Patient, Void> actionCol;
    @FXML private TextField searchField;
    private ObservableList<Patient> masterData = FXCollections.observableArrayList();
    private FilteredList<Patient> filteredData;
    private PatientDao dao = new PatientDao();
    private User currentUser;
    public void initData(User user) { this.currentUser = user; setupTable(); loadData(); searchField.textProperty().addListener((obs, oldV, newV) -> applyFilter(newV)); }
    private void setupTable() {
        caseNoCol.setCellValueFactory(new PropertyValueFactory<>("caseNo"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        dateAddedCol.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        actionCol.setCellFactory(col -> new TableCell<Patient, Void>() {
            private final Button viewBtn = new Button("View");
            { viewBtn.setOnAction(e -> { Patient p = getTableView().getItems().get(getIndex()); onView(p); }); }
            protected void updateItem(Void v, boolean empty) { super.updateItem(v, empty); if (empty) setGraphic(null); else setGraphic(viewBtn); }
        });
    }
    public void loadData() { try { masterData.setAll(dao.findAll()); filteredData = new FilteredList<>(masterData, p -> true); table.setItems(filteredData); } catch (Exception ex) { ex.printStackTrace(); } }
    private void applyFilter(String text) { if (text == null || text.isEmpty()) { filteredData.setPredicate(p -> true); return; } final String lower = text.toLowerCase(); filteredData.setPredicate(p -> { if (p.caseNo != null && p.caseNo.toLowerCase().contains(lower)) return true; if (p.firstName != null && p.firstName.toLowerCase().contains(lower)) return true; if (p.lastName != null && p.lastName.toLowerCase().contains(lower)) return true; if (p.middleName != null && p.middleName.toLowerCase().contains(lower)) return true; if (p.dateAdded != null && p.dateAdded.toLowerCase().contains(lower)) return true; if (p.status != null && p.status.toLowerCase().contains(lower)) return true; return false; }); }
    @FXML public void onAdd() { openEditDialog(null); }
    @FXML public void onRefresh() { loadData(); }
    private void onView(Patient p) { openEditDialog(p); }
    private void openEditDialog(Patient p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_record.fxml"));
            Parent root = loader.load();
            EditRecordController ctrl = loader.getController();
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(p == null ? "Add Patient" : "Edit Patient");
            dialog.setScene(new javafx.scene.Scene(root));
            ctrl.initData(p);
            dialog.showAndWait();
            loadData();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
