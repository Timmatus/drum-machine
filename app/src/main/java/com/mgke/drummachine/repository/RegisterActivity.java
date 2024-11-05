package com.mgke.drummachine.repository;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.drummachine.MainActivity;
import com.mgke.drummachine.R;
import com.mgke.drummachine.model.User;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private UserRepository userRepository;
    private EditText editNickName;
    private EditText editEmail;
    private EditText editPassword;
    private Button registerButton;
    private TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userRepository = new UserRepository(db.collection("users"));

        editNickName = findViewById(R.id.username_et);
        editEmail = findViewById(R.id.email_et);
        editPassword = findViewById(R.id.password_et);
        registerButton = findViewById(R.id.sign_up_btn);
        loginTextView = findViewById(R.id.login_texview);

        registerButton.setOnClickListener(this::onClickRegistration);
        loginTextView.setOnClickListener(v -> {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
         startActivity(intent);
       });
    }

    public void onClickRegistration(View view) {
        String name = editNickName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (!Pattern.compile(emailPattern).matcher(email).matches()) {
            Toast.makeText(this, "Неправильный формат электронной почты", Toast.LENGTH_SHORT).show();
            return;
        }

        userRepository.addUser(name, email, password).thenAccept(result -> {
            if (result) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
            }
        }).exceptionally(e -> {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        });
    }
}
