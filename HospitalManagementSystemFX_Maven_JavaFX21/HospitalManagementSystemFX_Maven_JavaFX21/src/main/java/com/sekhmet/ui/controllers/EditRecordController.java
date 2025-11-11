
package com.sekhmet.ui.controllers;
import com.sekhmet.dao.PatientDao;
import com.sekhmet.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
public class EditRecordController {
    @FXML TextField caseNoField, lastField, firstField, middleField, dateField, statusField;
    @FXML ListView<String> hospList, medList, docList;
    private PatientDao dao = new PatientDao();
    private Patient patient;
    private List<Hospitalization> hospObjs = new ArrayList<>();
    private List<Medication> medObjs = new ArrayList<>();
    private List<com.sekhmet.model.Document> docs = new ArrayList<>();
    public void initData(Patient p) { this.patient = p; if (p != null) { caseNoField.setText(p.caseNo); lastField.setText(p.lastName); firstField.setText(p.firstName); middleField.setText(p.middleName); dateField.setText(p.dateAdded); statusField.setText(p.status); loadExtras(); } else { dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date())); } }
    private void loadExtras() { try { hospObjs = dao.loadHospitalizations(patient.id); medObjs = dao.loadMedications(patient.id); docs = dao.loadDocuments(patient.id); hospList.getItems().setAll(hospObjs.stream().map(h -> h.date + " | " + h.type + " | " + h.place).toList()); medList.getItems().setAll(medObjs.stream().map(m -> m.prescribedDate + " | " + m.medicine + " | " + m.dosage).toList()); docList.getItems().setAll(docs.stream().map(d -> d.filename + " (" + d.uploadedAt + ")").toList()); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML public void onAddHospital() { TextInputDialog d = new TextInputDialog(); d.setHeaderText("Format: yyyy-MM-dd | Type | Place"); d.showAndWait().ifPresent(txt -> { String[] parts = txt.split("\\|"); if (parts.length >= 3) { Hospitalization h = new Hospitalization(-1, parts[0].trim(), parts[1].trim(), parts[2].trim()); hospObjs.add(h); hospList.getItems().add(h.date + " | " + h.type + " | " + h.place); } }); }
    @FXML public void onDeleteHospital() { int idx = hospList.getSelectionModel().getSelectedIndex(); if (idx >= 0) { hospObjs.remove(idx); hospList.getItems().remove(idx); } }
    @FXML public void onAddMedication() { TextInputDialog d = new TextInputDialog(); d.setHeaderText("Format: yyyy-MM-dd | Medicine | Dosage"); d.showAndWait().ifPresent(txt -> { String[] parts = txt.split("\\|"); if (parts.length >= 3) { Medication m = new Medication(-1, parts[0].trim(), parts[1].trim(), parts[2].trim()); medObjs.add(m); medList.getItems().add(m.prescribedDate + " | " + m.medicine + " | " + m.dosage); } }); }
    @FXML public void onDeleteMedication() { int idx = medList.getSelectionModel().getSelectedIndex(); if (idx >= 0) { medObjs.remove(idx); medList.getItems().remove(idx); } }
    @FXML public void onUploadDoc() { try { FileChooser fc = new FileChooser(); File f = fc.showOpenDialog(caseNoField.getScene().getWindow()); if (f != null) { byte[] data = Files.readAllBytes(f.toPath()); if (patient == null) { Patient p = new Patient(-1, caseNoField.getText().trim(), lastField.getText().trim(), firstField.getText().trim(), middleField.getText().trim(), dateField.getText().trim(), statusField.getText().trim()); patient = dao.insert(p); } dao.insertDocument(patient.id, f.getName(), data); loadExtras(); } } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML public void onDownloadDoc() { int idx = docList.getSelectionModel().getSelectedIndex(); if (idx >= 0) { try { com.sekhmet.model.Document d = docs.get(idx); FileChooser fc = new FileChooser(); fc.setInitialFileName(d.filename); File chosen = fc.showSaveDialog(caseNoField.getScene().getWindow()); if (chosen != null) Files.write(chosen.toPath(), d.data); } catch (Exception ex) { ex.printStackTrace(); } } }
    @FXML public void onDeletePatient() { TextInputDialog pwd = new TextInputDialog(); pwd.setHeaderText("Enter admin password to delete permanently:"); pwd.showAndWait().ifPresent(pw -> { if (!"admin123".equals(pw)) { Alert a = new Alert(Alert.AlertType.ERROR, "Incorrect admin password."); a.showAndWait(); return; } if (patient != null) { try { dao.delete(patient.id); ((Stage) caseNoField.getScene().getWindow()).close(); } catch (Exception ex) { ex.printStackTrace(); } } }); }
    @FXML public void onSave() { try { if (patient == null) { Patient p = new Patient(-1, caseNoField.getText().trim(), lastField.getText().trim(), firstField.getText().trim(), middleField.getText().trim(), dateField.getText().trim(), statusField.getText().trim()); patient = dao.insert(p); } else { patient.caseNo = caseNoField.getText().trim(); patient.lastName = lastField.getText().trim(); patient.firstName = firstField.getText().trim(); patient.middleName = middleField.getText().trim(); patient.dateAdded = dateField.getText().trim(); patient.status = statusField.getText().trim(); dao.update(patient); } for (Hospitalization h : hospObjs) dao.insertHospitalization(patient.id, h); for (Medication m : medObjs) dao.insertMedication(patient.id, m); Alert a = new Alert(Alert.AlertType.INFORMATION, "Saved."); a.showAndWait(); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML public void onClose() { ((Stage) caseNoField.getScene().getWindow()).close(); }
}
