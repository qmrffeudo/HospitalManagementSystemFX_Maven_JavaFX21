package com.sekhmet.dao;

import com.sekhmet.db.DB;
import com.sekhmet.model.*;

import java.sql.*;
import java.util.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;

public class PatientDao {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // ✅ Load all patients
    public List<Patient> findAll() throws SQLException {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY id";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Patient(
                        rs.getInt("id"),
                        rs.getString("case_no"),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getString("date_added"),
                        rs.getString("status")
                ));
            }
        }
        return list;
    }

    // ✅ Insert new patient (auto date if null)
    public Patient insert(Patient p) throws SQLException {
        if (p.dateAdded == null || p.dateAdded.isEmpty()) {
            p.dateAdded = DATE_FORMAT.format(new java.util.Date());
        }

        String sql = "INSERT INTO patients(case_no, last_name, first_name, middle_name, date_added, status) VALUES(?,?,?,?,?,?)";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.caseNo);
            ps.setString(2, p.lastName);
            ps.setString(3, p.firstName);
            ps.setString(4, p.middleName);
            ps.setString(5, p.dateAdded);
            ps.setString(6, p.status);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.id = rs.getInt(1);
            }
        }
        return p;
    }

    // ✅ Update patient
    public void update(Patient p) throws SQLException {
        String sql = "UPDATE patients SET case_no=?, last_name=?, first_name=?, middle_name=?, date_added=?, status=? WHERE id=?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.caseNo);
            ps.setString(2, p.lastName);
            ps.setString(3, p.firstName);
            ps.setString(4, p.middleName);
            ps.setString(5, p.dateAdded);
            ps.setString(6, p.status);
            ps.setInt(7, p.id);
            ps.executeUpdate();
        }
    }

    // ✅ Delete patient (with cascade cleanup)
    public void delete(int id) throws SQLException {
        try (Connection c = DB.getConnection()) {
            c.setAutoCommit(false);
            try {
                // Delete related data
                try (PreparedStatement ps1 = c.prepareStatement("DELETE FROM documents WHERE patient_id=?");
                     PreparedStatement ps2 = c.prepareStatement("DELETE FROM hospitalizations WHERE patient_id=?");
                     PreparedStatement ps3 = c.prepareStatement("DELETE FROM medications WHERE patient_id=?");
                     PreparedStatement ps4 = c.prepareStatement("DELETE FROM patients WHERE id=?")) {

                    ps1.setInt(1, id);
                    ps1.executeUpdate();
                    ps2.setInt(1, id);
                    ps2.executeUpdate();
                    ps3.setInt(1, id);
                    ps3.executeUpdate();
                    ps4.setInt(1, id);
                    ps4.executeUpdate();
                }

                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    // ✅ Hospitalization methods
    public void insertHospitalization(int patientId, Hospitalization h) throws SQLException {
        String sql = "INSERT INTO hospitalizations(patient_id, date, type, place) VALUES(?,?,?,?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setString(2, h.date);
            ps.setString(3, h.type);
            ps.setString(4, h.place);
            ps.executeUpdate();
        }
    }

    // ✅ Medication methods
    public void insertMedication(int patientId, Medication m) throws SQLException {
        String sql = "INSERT INTO medications(patient_id, prescribed_date, medicine, dosage) VALUES(?,?,?,?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setString(2, m.prescribedDate);
            ps.setString(3, m.medicine);
            ps.setString(4, m.dosage);
            ps.executeUpdate();
        }
    }

    public List<Hospitalization> loadHospitalizations(int patientId) throws SQLException {
        List<Hospitalization> list = new ArrayList<>();
        String sql = "SELECT * FROM hospitalizations WHERE patient_id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Hospitalization(
                            rs.getInt("id"),
                            rs.getString("date"),
                            rs.getString("type"),
                            rs.getString("place")
                    ));
                }
            }
        }
        return list;
    }

    public List<Medication> loadMedications(int patientId) throws SQLException {
        List<Medication> list = new ArrayList<>();
        String sql = "SELECT * FROM medications WHERE patient_id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Medication(
                            rs.getInt("id"),
                            rs.getString("prescribed_date"),
                            rs.getString("medicine"),
                            rs.getString("dosage")
                    ));
                }
            }
        }
        return list;
    }

    // ✅ Document management
    public void insertDocument(int patientId, String filename, byte[] data) throws Exception {
        String sql = "INSERT INTO documents(patient_id, filename, data, uploaded_at) VALUES(?,?,?,?)";

        // Ensure folders exist
        Files.createDirectories(Paths.get("documents"));

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setString(2, filename);
            ps.setBytes(3, data);
            ps.setString(4, DATE_FORMAT.format(new java.util.Date()));
            ps.executeUpdate();
        }

        // Save physical file copy
        Path filePath = Paths.get("documents", patientId + "_" + filename);
        Files.write(filePath, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public List<Document> loadDocuments(int patientId) throws SQLException {
        List<Document> list = new ArrayList<>();
        String sql = "SELECT id, filename, uploaded_at, data FROM documents WHERE patient_id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Document(
                            rs.getInt("id"),
                            rs.getString("filename"),
                            rs.getBytes("data"),
                            rs.getString("uploaded_at")
                    ));
                }
            }
        }
        return list;
    }
}
