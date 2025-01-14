    package com.mgke.drummachine.repository;

    import android.content.Intent;
    import android.content.SharedPreferences;
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
    import com.mgke.drummachine.UserProfileActivity;
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
        // Сохранение userId в SharedPreferences
        private void saveUserId(String userId) {
            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("userId", userId);
            editor.apply();
        }
        public void onClickRegistration(View view) {
            String name = editNickName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 8 || !Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) {
                Toast.makeText(this, "Пароль должен быть не менее 8 символов и содержать хотя бы один специальный символ", Toast.LENGTH_SHORT).show();
                return;
            }
            // Проверка уникальности почты и логина
            userRepository.isUserUnique(name, email).thenAccept(isUnique -> {
                if (isUnique) {
                    // Почта и логин уникальны, регистрируем пользователя
                    userRepository.addUser(name, email, password).thenAccept(result -> {
                        if (result) {
                            userRepository.getUserByEmail(email, password).thenAccept(user -> {
                                if (user != null) {
                                    saveUserId(user.id);

                                    Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(this, "Ошибка при получении данных пользователя", Toast.LENGTH_SHORT).show();
                                }
                            }).exceptionally(e -> {
                                Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                return null;
                            });
                        } else {
                            Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                        }
                    }).exceptionally(e -> {
                        Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return null;
                    });
                } else {
                    Toast.makeText(this, "Почта или логин уже используются", Toast.LENGTH_SHORT).show();
                }
            }).exceptionally(e -> {
                Toast.makeText(this, "Ошибка проверки уникальности: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return null;
            });
        }
        @Override
        public void onBackPressed() {
            // Оставьте пустым, чтобы ничего не делать при нажатии кнопки Назад
            // super.onBackPressed(); // Не вызывайте суперкласс, чтобы отключить стандартное поведение

        }



    }
