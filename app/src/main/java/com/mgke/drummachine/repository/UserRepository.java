package com.mgke.drummachine.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mgke.drummachine.model.User;

import java.util.concurrent.CompletableFuture;

public class UserRepository {
    private final CollectionReference userCollection;

    public UserRepository(CollectionReference userCollection) {
        this.userCollection = userCollection;
    }

    public CompletableFuture<Boolean> addUser(String username, String email, String password) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        String userID = userCollection.document().getId();
        String avatarUrl = null;

        User user = new User(userID, username, password, email, avatarUrl);

        userCollection.document(userID).set(user)
                .addOnSuccessListener(aVoid -> future.complete(true))
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }

    public CompletableFuture<User> getUserByEmail(String email, String password) {
        CompletableFuture<User> future = new CompletableFuture<>();

        userCollection.whereEqualTo("mail", email)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    User user = null;
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        user = document.toObject(User.class);
                        break;
                    }
                    future.complete(user);
                })
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }
    public CompletableFuture<Void> addToFollowedUsers(String userId) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        // Получить текущего пользователя (замените на реальный метод получения userId текущего пользователя)
        String currentUserId = "current_user_id";

        userCollection.document(currentUserId)
                .update("followedUsers", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> future.complete(null))
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }
    public CompletableFuture<Boolean> isUserUnique(String username, String email) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        // Проверка уникальности почты
        userCollection.whereEqualTo("mail", email).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Почта уже существует
                        future.complete(false);
                        return;
                    }

                    // Проверка уникальности логина
                    userCollection.whereEqualTo("username", username).get()
                            .addOnSuccessListener(querySnapshot2 -> {
                                if (!querySnapshot2.isEmpty()) {
                                    // Логин уже существует
                                    future.complete(false);
                                } else {
                                    // Почта и логин уникальны
                                    future.complete(true);
                                }
                            })
                            .addOnFailureListener(future::completeExceptionally);
                })
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }

}
