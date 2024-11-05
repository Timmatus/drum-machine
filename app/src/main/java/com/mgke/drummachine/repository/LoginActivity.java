package com.mgke.drummachine.repository;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mgke.drummachine.MainActivity;
import com.mgke.drummachine.R;

public class LoginActivity extends AppCompatActivity {

    private UserRepository userRepository;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userRepository = new UserRepository(db.collection("users"));

        emailEditText = findViewById(R.id.email_et);
        passwordEditText = findViewById(R.id.password_et);
        loginButton = findViewById(R.id.login_btn);
        registerBtn = findViewById(R.id.reg_texview);
        loginButton.setOnClickListener(this::onLoginClicked);

        registerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }



    private void onLoginClicked(View view) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        userRepository.getUserByEmail(email, password)
                .thenAccept(user -> {
                    if (user != null) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Неправильный email или пароль", Toast.LENGTH_SHORT).show();
                    }
                })
                .exceptionally(e -> {
                    Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return null;
                });
    }
}
