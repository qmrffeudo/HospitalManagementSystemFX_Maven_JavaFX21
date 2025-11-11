
package com.sekhmet.db;
import java.nio.file.*;
import java.sql.*;
public class DB {
    public static final String DB_FILE = "hospital.db";
    public static final String URL = "jdbc:sqlite:" + DB_FILE;
    static { try { Files.createDirectories(Paths.get("backups")); Files.createDirectories(Paths.get("documents")); } catch(Exception e){} }
    public static Connection getConnection() throws SQLException { return DriverManager.getConnection(URL); }
    public static void init() {
        String[] stmts = {
            "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, role TEXT);",
            "CREATE TABLE IF NOT EXISTS patients (id INTEGER PRIMARY KEY AUTOINCREMENT, case_no TEXT UNIQUE, last_name TEXT, first_name TEXT, middle_name TEXT, date_added TEXT, status TEXT);",
            "CREATE TABLE IF NOT EXISTS hospitalizations (id INTEGER PRIMARY KEY AUTOINCREMENT, patient_id INTEGER, date TEXT, type TEXT, place TEXT);",
            "CREATE TABLE IF NOT EXISTS medications (id INTEGER PRIMARY KEY AUTOINCREMENT, patient_id INTEGER, prescribed_date TEXT, medicine TEXT, dosage TEXT);",
            "CREATE TABLE IF NOT EXISTS documents (id INTEGER PRIMARY KEY AUTOINCREMENT, patient_id INTEGER, filename TEXT, data BLOB, uploaded_at TEXT);"
        };
        try (Connection c = getConnection(); Statement s = c.createStatement()) { for(String st:stmts) s.execute(st); } catch(SQLException ex){ ex.printStackTrace(); }
    }
}
