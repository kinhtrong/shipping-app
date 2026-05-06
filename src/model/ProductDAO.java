package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductDAO {

    // Hàm Save (Lưu vào Database)
    public boolean saveProduct(Product product) {
        String sql = "INSERT INTO products (id, name, price, in_stock) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, product.getId());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setBoolean(4, product.isInStock());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu lưu thành công
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm Load (Lấy dữ liệu từ Database theo ID)
    public Product getProductById(String id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                boolean inStock = rs.getBoolean("in_stock");
                
                return new Product(id, name, price, inStock);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu không tìm thấy
    }
}