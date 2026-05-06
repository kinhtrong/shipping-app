import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Đọc file giao diện FXML (Đảm bảo tên file ở đây khớp với tên file bạn đã lưu)
        Parent root = FXMLLoader.load(getClass().getResource("ProductForm.fxml"));
        
        // Thiết lập tiêu đề cho cửa sổ
        primaryStage.setTitle("Hệ Thống Quản Lý Kho - Tích hợp Database");
        
        // Thiết lập kích thước khung hình (rộng 500, cao 450)
        primaryStage.setScene(new Scene(root, 500, 450));
        
        // (Tuỳ chọn) Khoá không cho người dùng kéo giãn cửa sổ làm vỡ layout
        primaryStage.setResizable(false); 
        
        // Hiển thị cửa sổ lên màn hình
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Lệnh launch sẽ tự động khởi động nền tảng JavaFX và gọi hàm start() ở trên
        launch(args);
    }
}