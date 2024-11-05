package com.mgke.drummachine.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.mgke.drummachine.model.User;

import java.util.concurrent.CompletableFuture;

public class UserRepository {
 private final CollectionReference userCollection;
 private final FirebaseFirestore db;


    public UserRepository(CollectionReference userCollection, FirebaseFirestore db) {
        this.userCollection = userCollection;
        this.db = db;
    }

    public CompletableFuture<User> addUser (User user){
        CompletableFuture<User> future = new CompletableFuture<>();
        String userID = userCollection.document().getId();
        user.id = userID;
        userCollection.document(userID).set(user);
        return future;
    }
}
