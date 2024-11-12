package com.mgke.drummachine.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mgke.drummachine.model.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SoundRepository {

    private static final String SOUNDS_COLLECTION = "sounds";
    private final CollectionReference soundsCollection;

    public SoundRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        soundsCollection = db.collection(SOUNDS_COLLECTION);
    }

    // Метод для сохранения звука
    public CompletableFuture<Boolean> saveSound(Sound sound) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        soundsCollection.document(sound.id)
                .set(sound)
                .addOnSuccessListener(aVoid -> future.complete(true))
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }

    // Метод для получения звука по ID
    public CompletableFuture<Sound> getSoundById(String soundId) {
        CompletableFuture<Sound> future = new CompletableFuture<>();

        soundsCollection.document(soundId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Sound sound = documentSnapshot.toObject(Sound.class);
                        future.complete(sound);
                    } else {
                        future.completeExceptionally(new Exception("Sound not found"));
                    }
                })
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }

    // Метод для получения всех звуков
    public CompletableFuture<List<Sound>> getAllSounds() {
        CompletableFuture<List<Sound>> future = new CompletableFuture<>();

        soundsCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Sound> sounds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Sound sound = document.toObject(Sound.class);
                        sounds.add(sound);
                    }
                    future.complete(sounds);
                })
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }

    // Метод для удаления звука по ID
    public CompletableFuture<Boolean> deleteSoundById(String soundId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        soundsCollection.document(soundId)
                .delete()
                .addOnSuccessListener(aVoid -> future.complete(true))
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }
}