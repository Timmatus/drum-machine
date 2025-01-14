package com.mgke.drummachine.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.drummachine.model.Subscription;

import java.util.concurrent.CompletableFuture;

public class SubscriptionRepository {

    private final CollectionReference subscriptionsCollection;

    public SubscriptionRepository() {
        this.subscriptionsCollection = FirebaseFirestore.getInstance().collection("subscriptions");
    }

    public CompletableFuture<Void> createSubscription(String subscriberId, String subscribedToId) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Subscription subscription = new Subscription(subscriberId, subscribedToId, new java.util.Date());

        subscriptionsCollection.add(subscription)
                .addOnSuccessListener(documentReference -> future.complete(null))
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }

    // Метод для проверки, подписан ли текущий пользователь на другого
    public CompletableFuture<Boolean> isUserSubscribed(String subscriberId, String subscribedToId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        subscriptionsCollection
                .whereEqualTo("subscriberId", subscriberId)
                .whereEqualTo("subscribedToId", subscribedToId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        future.complete(true);
                    } else {
                        future.complete(false);
                    }
                })
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }
}
