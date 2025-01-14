package com.mgke.drummachine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mgke.drummachine.model.User;
import com.mgke.drummachine.model.Sound;
import com.mgke.drummachine.repository.SoundRepository;
import com.mgke.drummachine.repository.SubscriptionRepository;
import com.mgke.drummachine.repository.UserRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OtherUserProfileActivity extends AppCompatActivity {

    private ImageView avatarImageView;
    private TextView userName;
    private RecyclerView soundsRecyclerView;
    private SoundAdapter soundAdapter;
    private FirebaseFirestore db;
    private String otherUserId, userId;
    private Button followButton;
    private SubscriptionRepository subscriptionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        CollectionReference userCollection = FirebaseFirestore.getInstance().collection("users");
        UserRepository userRepository = new UserRepository(userCollection);
        subscriptionRepository = new SubscriptionRepository();

        avatarImageView = findViewById(R.id.other_user_avatar);
        userName = findViewById(R.id.other_user_name);
        followButton = findViewById(R.id.follow_button);
        soundsRecyclerView = findViewById(R.id.sounds_recycler_view);
        soundsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userId = getUserIdFromPreferences();
        db = FirebaseFirestore.getInstance();



        otherUserId = getIntent().getStringExtra("userId");
        soundAdapter = new SoundAdapter(this, new ArrayList<>());
        soundsRecyclerView.setAdapter(soundAdapter);

        if (otherUserId != null) {
            loadUserProfile(otherUserId);
            loadUserSounds(otherUserId);
        }

        followButton.setOnClickListener(view -> followUser(otherUserId));
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Завершить текущую активность
    }

    private void loadUserProfile(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        userName.setText(user.username);
                        if (user.avatarUrl != null) {
                            Glide.with(this).load(user.avatarUrl).into(avatarImageView);
                        }
                    }
                });
    }

    private void loadUserSounds(String otherUserId) {
        SoundRepository soundRepository = new SoundRepository();

        // Загружаем все звуки и фильтруем по userId
        soundRepository.getAllSounds().thenAccept(sounds -> {
            List<Sound> userSounds = sounds.stream()
                    .filter(sound -> sound.getUserID().equals(otherUserId)) // Фильтруем звуки по otherUserId
                    .collect(Collectors.toList());

            runOnUiThread(() -> displaySounds(userSounds)); // Отображаем звуки в основном потоке
        }).exceptionally(e -> {
            runOnUiThread(() ->
                    Toast.makeText(this, "Ошибка загрузки звуков: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
            return null;
        });
    }

    // Отображение звуков в RecyclerView
    private void displaySounds(List<Sound> sounds) {
        soundAdapter.updateSounds(sounds); // Обновляем список звуков в адаптере
    }

        private void followUser(String userIdToFollow) {

        if (userId == null) {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        subscriptionRepository.isUserSubscribed(userId, userIdToFollow)
                .thenAccept(isSubscribed -> {
                    if (!isSubscribed) {
                        // Если еще не подписан, создаем подписку
                        subscriptionRepository.createSubscription(userId, userIdToFollow)
                                .thenAccept(aVoid -> Toast.makeText(this, "Вы подписались на пользователя", Toast.LENGTH_SHORT).show())
                                .exceptionally(e -> {
                                    Toast.makeText(this, "Ошибка при подписке", Toast.LENGTH_SHORT).show();
                                    return null;
                                });
                    } else {
                        Toast.makeText(this, "Вы уже подписаны на этого пользователя", Toast.LENGTH_SHORT).show();
                    }
                })
                .exceptionally(e -> {
                    Toast.makeText(this, "Ошибка проверки подписки", Toast.LENGTH_SHORT).show();
                    return null;
                });
    }
    private String getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.getString("userId", null);
    }

}
