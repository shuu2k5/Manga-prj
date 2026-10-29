package com.example.managa_manager_v3;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class StartApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Bật tính năng lưu trữ Offline của Firebase tại đây
        // Lệnh này sẽ chạy đúng 1 lần duy nhất khi app vừa mở lên
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}