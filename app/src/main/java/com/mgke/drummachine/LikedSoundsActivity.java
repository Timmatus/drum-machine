package com.mgke.drummachine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.drummachine.repository.LikedSoundRepository;
import com.mgke.drummachine.repository.SoundRepository;
import com.mgke.drummachine.model.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LikedSoundsActivity extends AppCompatActivity {

    private RecyclerView likedSoundsRecyclerView;
    private LikedSoundAdapter likedSoundAdapter;
    private FirebaseFirestore db;
    private LikedSoundRepository likedSoundRepository;
    private SoundRepository soundRepository;  // To fetch actual sound data
    private String userId;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_sounds);

        likedSoundsRecyclerView = findViewById(R.id.liked_sounds_recycler_view);
        likedSoundsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        likedSoundRepository = new LikedSoundRepository();
        soundRepository = new SoundRepository();
        userId = getUserIdFromPreferences();
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(view -> {
            // Возвращаемся на UserProfileActivity
            Intent intent = new Intent(LikedSoundsActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish(); // Завершаем текущую активность
        });
        loadLikedSounds();
    }

    // Fetch all the liked sound IDs, then retrieve the full sound objects
    private void loadLikedSounds() {
        likedSoundRepository.getFavoriteSoundsForUser(userId)
                .thenCompose(likedSounds -> {
                    List<CompletableFuture<Sound>> soundFutures = new ArrayList<>();

                    for (int i = 0; i < likedSounds.size(); i++) {
                        String soundId = likedSounds.get(i).getSoundid();
                        CompletableFuture<Sound> soundFuture = soundRepository.getSoundById(soundId);
                        soundFutures.add(soundFuture);
                    }

                    return CompletableFuture.allOf(soundFutures.toArray(new CompletableFuture[0]))
                            .thenApply(v -> {
                                List<Sound> sounds = new ArrayList<>();
                                for (CompletableFuture<Sound> future : soundFutures) {
                                    try {
                                        sounds.add(future.get()); // Fetch the actual sound
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                return sounds;
                            });
                })
                .thenAccept(sounds -> {
                    // Check if the data is loaded properly
                    if (sounds.isEmpty()) {
                        Log.d("LikedSoundsActivity", "No liked sounds found");
                    } else {
                        Log.d("LikedSoundsActivity", "Loaded " + sounds.size() + " liked sounds");
                    }

                    // Set up the adapter with the fetched sounds
                    likedSoundAdapter = new LikedSoundAdapter(this, sounds, userId);
                    likedSoundsRecyclerView.setAdapter(likedSoundAdapter);
                })
                .exceptionally(throwable -> {
                    Log.e("LikedSoundsActivity", "Error loading liked sounds", throwable);
                    Toast.makeText(LikedSoundsActivity.this, "Ошибка загрузки избранных звуков", Toast.LENGTH_SHORT).show();
                    return null;
                });
    }

    // Retrieve the userId from SharedPreferences
    private String getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.getString("userId", null);
    }
}
