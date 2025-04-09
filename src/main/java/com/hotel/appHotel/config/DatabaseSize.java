package com.hotel.appHotel.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseSize {

    public static long getDatabaseSize(String dbUrl) {
        long size = 0;
        try (Connection conn = DriverManager.getConnection(dbUrl);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT (page_count * page_size) AS size_in_bytes FROM pragma_page_count(), pragma_page_size();")) {
            if (rs.next()) {
                size = rs.getLong("size_in_bytes");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
}
