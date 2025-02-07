package com.mgke.drummachine.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mgke.drummachine.model.LikedSound;
import com.mgke.drummachine.model.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LikedSoundRepository {

    private final CollectionReference likedSoundsCollection;

    public LikedSoundRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        likedSoundsCollection = db.collection("liked_sounds");  // Коллекция с избранными звуками
    }

    // Метод для добавления звука в избранное
    public CompletableFuture<Void> addSoundToLiked(String userId, String soundId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        String likeid =likedSoundsCollection.document().getId();
        likedSoundsCollection.add(new LikedSound(likeid, userId, soundId))
                .addOnSuccessListener(documentReference -> future.complete(null))
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }

    // Метод для удаления звука из избранного
    public CompletableFuture<Void> removeSoundFromLiked(String userId, String soundId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        likedSoundsCollection.whereEqualTo("userid", userId)
                .whereEqualTo("soundid", soundId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    queryDocumentSnapshots.getDocuments().forEach(document -> {
                        document.getReference().delete();
                    });
                    future.complete(null);
                })
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }

    // Метод для проверки, добавлен ли звук в избранное
    public CompletableFuture<Boolean> isSoundLiked(String userId, String soundId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        likedSoundsCollection.whereEqualTo("userid", userId)
                .whereEqualTo("soundid", soundId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    future.complete(!queryDocumentSnapshots.isEmpty());
                })
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }

    public CompletableFuture<List<LikedSound>> getFavoriteSoundsForUser(String userId) {
        CompletableFuture<List<LikedSound>> future = new CompletableFuture<>();

        likedSoundsCollection.whereEqualTo("userid", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<LikedSound> likedSounds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        LikedSound likedSound = document.toObject(LikedSound.class);
                        likedSounds.add(likedSound);
                    }
                    future.complete(likedSounds);
                })
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }



}
