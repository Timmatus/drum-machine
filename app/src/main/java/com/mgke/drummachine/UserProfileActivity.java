package com.mgke.drummachine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import android.content.SharedPreferences;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.provider.MediaStore;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.drummachine.model.User;
import com.mgke.drummachine.repository.LoginActivity;
import com.mgke.drummachine.repository.UserRepository;

public class UserProfileActivity extends AppCompatActivity {

    private TextView userName;
    private ImageView avatarImageView;
    private FirebaseFirestore db;
    private String userId;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private CloudinaryUploadImage cloudinaryUploadImage;
    private Button userSoundsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Button logoutButton = findViewById(R.id.logout_button);
        Button subscriptionsButton = findViewById(R.id.subscriptions_button);
        Button searchUsersButton = findViewById(R.id.search_users_button);
        Button drumPadButton = findViewById(R.id.drum_pad);
        Button likedSound = findViewById(R.id.liked_sounds_button);
        userName = findViewById(R.id.user_name);
        avatarImageView = findViewById(R.id.user_avatar);
        db = FirebaseFirestore.getInstance();
        cloudinaryUploadImage = new CloudinaryUploadImage(this);

        userId = getUserIdFromPreferences();
        Button userSoundsButton = findViewById(R.id.user_sounds_button);

        userSoundsButton.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, UserSoundsActivity.class);
            startActivity(intent);
        });


        if (userId != null) {
            loadUserProfile(userId);
        } else {
            userName.setText("ID пользователя не найден");
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            uploadAvatar(imageUri);
                        }
                    }
                }
        );

        avatarImageView.setOnClickListener(view -> openImagePicker());

        drumPadButton.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, SecondActivity.class);
            startActivity(intent);
        });

        searchUsersButton.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, SearchUsersActivity.class);
            startActivity(intent);
        });

        likedSound.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, LikedSoundsActivity.class);
            startActivity(intent);
        });

        subscriptionsButton.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, SubscriptionsActivity.class);
            startActivity(intent);
        });
        logoutButton.setOnClickListener(view -> {
            logout();
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void uploadAvatar(Uri imageUri) {
        cloudinaryUploadImage.uploadImage(imageUri, userId, imageUrl -> {
            if (imageUrl != null) {
                updateUserProfileImage(imageUrl);
            } else {
                Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfileImage(String imageUrl) {
        db.collection("users").document(userId)
                .update("avatarUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    // Обновляем изображение в UI
                    Glide.with(this).load(imageUrl).into(avatarImageView);
                    Toast.makeText(this, "Аватарка обновлена", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Ошибка обновления профиля", Toast.LENGTH_SHORT).show());
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
                            if (user.avatarUrl != null) {
                                Glide.with(this).load(user.avatarUrl).into(avatarImageView);
                            }
                        }
                    } else {
                        userName.setText("Пользователь не найден");
                    }
                })
                .addOnFailureListener(e -> userName.setText("Ошибка загрузки профиля"));
    }
    private void logout() {
        // Удаляем сохранённый userId из SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("userId");
        editor.apply();

        // Переход на экран входа
        Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Показываем сообщение
        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        // Оставьте пустым, чтобы ничего не делать при нажатии кнопки Назад
        // super.onBackPressed(); // Не вызывайте суперкласс, чтобы отключить стандартное поведение

    }
}
