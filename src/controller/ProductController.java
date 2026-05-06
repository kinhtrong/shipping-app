package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import model.Product;
import model.ProductDAO;

public class ProductController {

    @FXML private TextField txtId;
    @FXML private TextField txtName;
    @FXML private TextField txtPrice;
    @FXML private CheckBox chkInStock;
    @FXML private Label lblMessage;
    
    // Ánh xạ thêm ProgressIndicator từ FXML
    @FXML private ProgressIndicator progressIndicator;

    private ProductDAO productDAO = new ProductDAO();

    // Hàm này tự động chạy khi Form vừa được load lên
    @FXML
    public void initialize() {
        
        // YÊU CẦU 1: Real-time Validation (Kiểm tra khi đang gõ)
        // Lắng nghe sự thay đổi trên ô Giá sản phẩm
        txtPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            // Nếu người dùng nhập ký tự không phải là số (hoặc dấu chấm thập phân)
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtPrice.setStyle("-fx-border-color: red; -fx-border-width: 2px;"); // Viền đỏ
                showFeedback("Cảnh báo: Giá sản phẩm chỉ được nhập số!", Color.RED);
            } else {
                txtPrice.setStyle(""); // Trả lại viền bình thường
                showFeedback("", Color.BLACK); // Xóa cảnh báo
            }
        });

        // YÊU CẦU 2.1: Sự kiện bàn phím (Enter để nhảy sang ô tiếp theo)
        txtId.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) txtName.requestFocus();
        });
        txtName.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) txtPrice.requestFocus();
        });
        txtPrice.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) chkInStock.requestFocus();
        });
        chkInStock.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) handleSubmit(null); // Gửi form
        });

        // YÊU CẦU 2.2: Phím tắt toàn cục (Ctrl + S để Lưu)
        // Phải dùng Platform.runLater để đảm bảo Scene đã được dựng xong thì mới gắn phím tắt được
        Platform.runLater(() -> {
            txtId.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), 
                () -> handleSubmit(null)
            );
        });
    }

    // YÊU CẦU 3 & 4: Xử lý đa luồng (Background Thread) cho tính năng SAVE
    @FXML
    public void handleSubmit(ActionEvent event) {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String priceString = txtPrice.getText().trim();
        boolean inStock = chkInStock.isSelected();

        // Validation cơ bản
        if (id.isEmpty() || name.isEmpty() || priceString.isEmpty()) {
            showFeedback("Lỗi: Vui lòng nhập đủ thông tin!", Color.RED);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceString);
        } catch (NumberFormatException e) {
            return; // Đã xử lý real-time báo đỏ ở hàm initialize rồi
        }

        // Bật vòng xoay tải (trên luồng giao diện chính)
        progressIndicator.setVisible(true);
        showFeedback("Đang lưu vào Database...", Color.BLUE);

        // Tạo luồng nền (Background Thread) để kết nối Database không làm đơ giao diện
        new Thread(() -> {
            try {
                // Giả lập mạng chậm 1.5 giây để bạn thấy rõ vòng xoay ProgressIndicator
                Thread.sleep(1500); 
            } catch (InterruptedException e) { }

            Product newProduct = new Product(id, name, price, inStock);
            boolean isSaved = productDAO.saveProduct(newProduct);

            // QUAN TRỌNG: Mọi cập nhật lên giao diện (ẩn xoay, in chữ) BẮT BUỘC phải đưa về luồng chính thông qua Platform.runLater()
            Platform.runLater(() -> {
                progressIndicator.setVisible(false); // Tắt vòng xoay
                
                if (isSaved) {
                    showFeedback("Lưu thành công: " + name, Color.GREEN);
                } else {
                    showFeedback("Lỗi: Không thể lưu (Trùng ID hoặc lỗi mạng)", Color.RED);
                }
            });

        }).start(); // Bắt đầu chạy luồng nền
    }

    // YÊU CẦU 3 & 4: Xử lý đa luồng (Background Thread) cho tính năng LOAD
    @FXML
    public void handleLoad(ActionEvent event) {
        String idToLoad = txtId.getText().trim();
        
        if (idToLoad.isEmpty()) {
            showFeedback("Vui lòng nhập Mã SP để tải!", Color.ORANGE);
            return;
        }

        progressIndicator.setVisible(true);
        showFeedback("Đang tìm kiếm trong Database...", Color.BLUE);

        // Đẩy tác vụ tìm kiếm xuống luồng nền
        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) {} // Giả lập delay

            Product product = productDAO.getProductById(idToLoad);

            // Trả kết quả về luồng giao diện
            Platform.runLater(() -> {
                progressIndicator.setVisible(false);
                
                if (product != null) {
                    txtName.setText(product.getName());
                    txtPrice.setText(String.valueOf(product.getPrice()));
                    chkInStock.setSelected(product.isInStock());
                    showFeedback("Tải dữ liệu thành công!", Color.GREEN);
                } else {
                    showFeedback("Không tìm thấy Sản phẩm!", Color.RED);
                    txtName.clear();
                    txtPrice.clear();
                }
            });
        }).start();
    }

    @FXML
    public void handleClear(ActionEvent event) {
        txtId.clear();
        txtName.clear();
        txtPrice.clear();
        chkInStock.setSelected(false);
        showFeedback("", Color.BLACK);
        txtPrice.setStyle("");
    }

    private void showFeedback(String message, Color color) {
        lblMessage.setText(message);
        lblMessage.setTextFill(color);
    }
}