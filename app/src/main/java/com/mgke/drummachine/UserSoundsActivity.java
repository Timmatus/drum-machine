package com.mgke.drummachine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mgke.drummachine.model.Sound;
import com.mgke.drummachine.repository.SoundRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class UserSoundsActivity extends AppCompatActivity {

    private RecyclerView soundsRecyclerView;
    private SoundRepository soundRepository;
    private SoundAdapter soundAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sounds);

        soundsRecyclerView = findViewById(R.id.sounds_recycler_view);
        soundsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        soundRepository = new SoundRepository();

        ImageButton backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(view -> {
            // Возвращаемся на UserProfileActivity
            Intent intent = new Intent(UserSoundsActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish(); // Завершаем текущую активность
        });

        loadUserSounds();
    }

    private void loadUserSounds() {
        String userId = getUserIdFromPreferences();
        soundRepository.getAllSounds().thenAccept(sounds -> {
            List<Sound> userSounds = sounds.stream()
                    .filter(sound -> sound.getUserID().equals(userId))
                    .collect(Collectors.toList());
            displaySounds(userSounds);
        }).exceptionally(e -> {
            Toast.makeText(this, "Ошибка загрузки звуков: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        });
    }

    private void displaySounds(List<Sound> sounds) {
        soundAdapter = new SoundAdapter(this, sounds);
        soundsRecyclerView.setAdapter(soundAdapter);
    }

    private String getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.getString("userId", null);
    }

}
