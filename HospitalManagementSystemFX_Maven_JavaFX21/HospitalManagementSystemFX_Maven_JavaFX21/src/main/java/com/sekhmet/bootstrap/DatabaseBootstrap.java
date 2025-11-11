
package com.sekhmet.bootstrap;
import com.sekhmet.db.DB;
import java.sql.*;
public class DatabaseBootstrap {
    public static void ensureDefaultAdmin() {
        DB.init();
        try (Connection c = DB.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM users")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        try (PreparedStatement ins = c.prepareStatement("INSERT INTO users(username, password, role) VALUES(?,?,?)")) {
                            ins.setString(1, "admin"); ins.setString(2, "admin123"); ins.setString(3, "ADMIN"); ins.executeUpdate();
                        }
                        try (PreparedStatement p = c.prepareStatement("INSERT INTO patients(case_no, last_name, first_name, middle_name, date_added, status) VALUES(?,?,?,?,?,?)")) {
                            p.setString(1, "P-001"); p.setString(2, "Reyes"); p.setString(3, "Juan"); p.setString(4, "D."); p.setString(5, "2025-10-20"); p.setString(6, "Active"); p.executeUpdate();
                        }
                        try (PreparedStatement p = c.prepareStatement("INSERT INTO patients(case_no, last_name, first_name, middle_name, date_added, status) VALUES(?,?,?,?,?,?)")) {
                            p.setString(1, "P-002"); p.setString(2, "Garcia"); p.setString(3, "Maria"); p.setString(4, "L."); p.setString(5, "2025-10-22"); p.setString(6, "Active"); p.executeUpdate();
                        }
                        try (PreparedStatement ph = c.prepareStatement("INSERT INTO hospitalizations(patient_id, date, type, place) VALUES(?,?,?,?)")) {
                            ph.setInt(1, 1); ph.setString(2, "2025-10-20"); ph.setString(3, "Surgery"); ph.setString(4, "Sekhmet General"); ph.executeUpdate();
                        }
                        try (PreparedStatement pm = c.prepareStatement("INSERT INTO medications(patient_id, prescribed_date, medicine, dosage) VALUES(?,?,?,?)")) {
                            pm.setInt(1, 1); pm.setString(2, "2025-10-21"); pm.setString(3, "Amoxicillin"); pm.setString(4, "500mg"); pm.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}
