package com.mgke.drummachine;

import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.drummachine.model.User;

public class UserProfileActivity extends AppCompatActivity {

    private TextView userName;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Button drumPadButton = findViewById(R.id.drum_pad);
        userName = findViewById(R.id.user_name);
        db = FirebaseFirestore.getInstance();

        // Получение userId из SharedPreferences
        String userId = getUserIdFromPreferences();

        if (userId != null) {
            loadUserProfile(userId);
        } else {
            userName.setText("ID пользователя не найден");
        }
        drumPadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Создаем Intent для перехода на SecondActivity
                Intent intent = new Intent(UserProfileActivity.this, SecondActivity.class);
                startActivity(intent); // Запускаем SecondActivity
            }
        });

    }



    // Метод для получения ID пользователя из SharedPreferences
    private String getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.getString("userId", null);
    }

    private void loadUserProfile(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            userName.setText(user.username);
                        }
                    } else {
                        userName.setText("Пользователь не найден");
                    }
                })
                .addOnFailureListener(e -> userName.setText("Ошибка загрузки профиля"));
    }


}