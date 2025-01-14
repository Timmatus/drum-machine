package com.mgke.drummachine;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mgke.drummachine.model.Comment;
import com.mgke.drummachine.repository.CommentRepository;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView commentsRecyclerView;
    private AllCommentsAdapter commentsAdapter;
    private CommentRepository commentRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commentsRecyclerView = findViewById(R.id.comments_recycler_view);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentRepository = new CommentRepository();
        commentsAdapter = new AllCommentsAdapter(new ArrayList<>(), this::deleteComment);
        commentsRecyclerView.setAdapter(commentsAdapter);

        loadComments();
    }

    private void loadComments() {
        commentRepository.getAllComments()
                .thenAccept(comments -> runOnUiThread(() -> commentsAdapter.updateComments(comments)))
                .exceptionally(e -> {
                    runOnUiThread(() -> Toast.makeText(this, "Ошибка загрузки комментариев: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    return null;
                });
    }

    private void deleteComment(String commentId) {
        commentRepository.deleteCommentById(commentId)
                .thenAccept(success -> runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this, "Комментарий удален", Toast.LENGTH_SHORT).show();
                        loadComments();
                    }
                }))
                .exceptionally(e -> {
                    runOnUiThread(() -> Toast.makeText(this, "Ошибка удаления: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    return null;
                });
    }
}
