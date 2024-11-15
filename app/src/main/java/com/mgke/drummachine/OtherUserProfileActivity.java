package com.mgke.drummachine;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mgke.drummachine.model.User;
import com.mgke.drummachine.model.Sound;
import com.mgke.drummachine.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class OtherUserProfileActivity extends AppCompatActivity {

    private ImageView avatarImageView;
    private TextView userName;
    private RecyclerView soundsRecyclerView;
    private SoundsAdapter soundsAdapter;
    private FirebaseFirestore db;
    private String otherUserId;
    private Button followButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        avatarImageView = findViewById(R.id.other_user_avatar);
        userName = findViewById(R.id.other_user_name);
        followButton = findViewById(R.id.follow_button);
        soundsRecyclerView = findViewById(R.id.sounds_recycler_view);
        soundsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        otherUserId = getIntent().getStringExtra("userId");
        soundsAdapter = new SoundsAdapter(new ArrayList<>());
        soundsRecyclerView.setAdapter(soundsAdapter);

        if (otherUserId != null) {
            loadUserProfile(otherUserId);
            loadUserSounds(otherUserId);
        }

        followButton.setOnClickListener(view -> followUser(otherUserId));
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

    private void loadUserSounds(String userId) {
        db.collection("sounds")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Sound> sounds = queryDocumentSnapshots.toObjects(Sound.class);
                    soundsAdapter.updateSounds(sounds);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Ошибка загрузки звуков", Toast.LENGTH_SHORT).show());
    }

    private void followUser(String userId) {
        UserRepository userRepository = new UserRepository();
        userRepository.addToFollowedUsers(userId)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Пользователь добавлен в отслеживаемые", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Ошибка при добавлении пользователя", Toast.LENGTH_SHORT).show());
    }
}
