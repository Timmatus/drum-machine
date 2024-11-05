package com.mgke.drummachine.repository;

import com.google.firebase.firestore.CollectionReference;
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
}
