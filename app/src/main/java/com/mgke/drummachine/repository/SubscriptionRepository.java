package com.mgke.drummachine.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.drummachine.model.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
    public CompletableFuture<Void> deleteSubscription(String subscriberId, String subscribedToId) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        subscriptionsCollection
                .whereEqualTo("subscriberId", subscriberId)
                .whereEqualTo("subscribedToId", subscribedToId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Удаляем все найденные документы
                        List<DocumentReference> documentsToDelete = querySnapshot.getDocuments()
                                .stream()
                                .map(documentSnapshot -> subscriptionsCollection.document(documentSnapshot.getId()))
                                .collect(Collectors.toList());

                        for (com.google.firebase.firestore.DocumentReference doc : documentsToDelete) {
                            doc.delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Завершаем CompletableFuture, если все прошло успешно
                                        if (documentsToDelete.indexOf(doc) == documentsToDelete.size() - 1) {
                                            future.complete(null);
                                        }
                                    })
                                    .addOnFailureListener(future::completeExceptionally);
                        }
                    } else {
                        future.completeExceptionally(new Exception("Подписка не найдена."));
                    }
                })
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }
    public CompletableFuture<List<Subscription>> getSubscriptionsForUser(String userId) {
        CompletableFuture<List<Subscription>> future = new CompletableFuture<>();

        subscriptionsCollection
                .whereEqualTo("subscriberId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Subscription> subscriptions = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Subscription subscription = document.toObject(Subscription.class);
                        subscriptions.add(subscription);
                    }
                    future.complete(subscriptions);
                })
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }
}
