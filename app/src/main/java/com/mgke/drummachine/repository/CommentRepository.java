package com.mgke.drummachine.repository;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mgke.drummachine.model.Comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommentRepository {

    private final CollectionReference commentsCollection;

    // Конструктор, который инициализирует ссылку на коллекцию комментариев
    public CommentRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        commentsCollection = db.collection("comments");
    }

    // Метод для добавления комментария
    public CompletableFuture<Boolean> addComment(Comment comment) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        // Генерация ID для комментария, если не задан
        if (comment.getSoundId() == null) {
            future.completeExceptionally(new IllegalArgumentException("Sound ID is required"));
            return future;
        }

        comment.setId(commentsCollection.document().getId());

        commentsCollection.document(comment.getId())
                .set(comment)
                .addOnSuccessListener(documentReference -> future.complete(true)) // Успешное добавление
                .addOnFailureListener(future::completeExceptionally); // Обработка ошибки

        return future;
    }

    // Метод для получения комментариев по ID звука
    public CompletableFuture<List<Comment>> getCommentsBySoundId(String soundId) {
        CompletableFuture<List<Comment>> future = new CompletableFuture<>();

        commentsCollection.whereEqualTo("soundId", soundId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Comment> comments = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Comment comment = document.toObject(Comment.class);
                        comments.add(comment);
                    }
                    Collections.sort(comments, Comparator.comparing(Comment::getTimestamp));
                    future.complete(comments);
                })
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }

    // Метод для удаления комментария по ID (если понадобится)
    public CompletableFuture<Boolean> deleteCommentById(String commentId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        commentsCollection.document(commentId)
                .delete()
                .addOnSuccessListener(aVoid -> future.complete(true))
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }

    public CompletableFuture<List<Comment>> getAllComments() {
        CompletableFuture<List<Comment>> future = new CompletableFuture<>();

        commentsCollection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Comment> comments = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Comment comment = document.toObject(Comment.class);
                        comments.add(comment);
                    }
                    future.complete(comments);
                })
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }
}
