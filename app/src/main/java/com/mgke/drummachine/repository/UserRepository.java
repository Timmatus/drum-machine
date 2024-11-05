package com.mgke.drummachine.repository;

import com.google.firebase.firestore.CollectionReference;
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
        User user = new User(userID, username, password, email);

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
}
