
package com.sekhmet.dao;
import com.sekhmet.db.DB;
import com.sekhmet.model.User;
import java.sql.*;
public class UserDao {
    public User findByUsername(String username) throws SQLException {
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE username=?")) {
            ps.setString(1, username); try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("role")); }
        }
        return null;
    }
}
