#!/bin/bash

PATH_TO_FX="lib"

echo "Đang biên dịch mã nguồn..."
# Biên dịch bình thường
javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml src/*.java

if [ $? -eq 0 ]; then
    echo "Biên dịch thành công! Đang khởi chạy ứng dụng..."
    echo "------------------------------------------------"
    
    # CHÚ Ý DÒNG DƯỚI ĐÂY: Thêm "lib/*" vào -cp để Java nhận diện được MySQL Connector
    # Trên Windows (Git Bash), dùng dấu chấm phẩy (;). Trên Mac/Linux dùng dấu hai chấm (:)
    java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -cp "src;lib/*" Main
else
    echo "------------------------------------------------"
    echo "Biên dịch thất bại."
fi