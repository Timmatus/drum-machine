package com.mgke.drummachine;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.drummachine.model.Comment;
import com.mgke.drummachine.model.Sound;
import com.mgke.drummachine.repository.CommentRepository;
import com.mgke.drummachine.repository.LikedSoundRepository;

import java.util.ArrayList;
import java.util.List;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.SoundViewHolder> {

    private final List<Sound> sounds;
    private final Context context;
    private final CommentRepository commentRepository;
    private final LikedSoundRepository likedSoundRepository;

    public SoundAdapter(Context context, List<Sound> sounds) {
        this.context = context;
        this.sounds = sounds;
        commentRepository = new CommentRepository();
        likedSoundRepository = new LikedSoundRepository();
    }

    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sound, parent, false);
        return new SoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position) {
        Sound sound = sounds.get(position);

        SharedPreferences prefs = context.getSharedPreferences("UserSession", MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        String soundId = sound.getId();
        holder.soundName.setText(sound.getSoundName());
        holder.soundAuthor.setText("Автор: " + sound.getUserID());
        holder.itemView.setOnClickListener(v -> playSound(sound.getSoundUrl()));
        likedSoundRepository.isSoundLiked(userId, soundId)
                .thenAccept(isLiked -> {
                    // Run UI update on the main thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (isLiked) {
                            holder.like_button.setText("Unlike");
                        } else {
                            holder.like_button.setText("Like");
                        }
                    });
                })
                .exceptionally(e -> {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "Error checking like status", Toast.LENGTH_SHORT).show();
                    });
                    return null;
                });

        // Handle like button click
        holder.like_button.setOnClickListener(v -> {
            if (userId != null) {
                likedSoundRepository.isSoundLiked(userId, sound.getId())
                        .thenAccept(isLiked -> {
                            if (isLiked) {
                                // Remove from favorites if already liked
                                likedSoundRepository.removeSoundFromLiked(userId, sound.getId())
                                        .thenAccept(aVoid -> {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                holder.like_button.setText("Like"); // Change button text to "Like"
                                                Toast.makeText(context, "Sound removed from favorites", Toast.LENGTH_SHORT).show();
                                            });
                                        })
                                        .exceptionally(e -> {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                Toast.makeText(context, "Error removing sound from favorites", Toast.LENGTH_SHORT).show();
                                            });
                                            return null;
                                        });
                            } else {
                                // Add to favorites if not liked
                                likedSoundRepository.addSoundToLiked(userId, sound.getId())
                                        .thenAccept(aVoid -> {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                holder.like_button.setText("Unlike"); // Change button text to "Unlike"
                                                Toast.makeText(context, "Sound added to favorites", Toast.LENGTH_SHORT).show();
                                            });
                                        })
                                        .exceptionally(e -> {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                Toast.makeText(context, "Error adding sound to favorites", Toast.LENGTH_SHORT).show();
                                            });
                                            return null;
                                        });
                            }
                        })
                        .exceptionally(e -> {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                Toast.makeText(context, "Error checking like status", Toast.LENGTH_SHORT).show();
                            });
                            return null;
                        });
            }
        });

        List<Comment> comments = new ArrayList<>();
        CommentAdapter commentAdapter = new CommentAdapter(context, comments);
        holder.recyclerView.setAdapter(commentAdapter);
        commentRepository.getCommentsBySoundId(sound.id).thenAccept(list -> {
            comments.addAll(list);
            commentAdapter.notifyItemInserted(comments.size());
        });

        holder.button.setOnClickListener(view -> {
            Comment comment = new Comment();
            comment.setContent(holder.editText.getText().toString());
            comment.setSoundId(sound.id);
            comment.setTimestamp(Timestamp.now());
            comment.setUserId(sound.userID);
            commentRepository.addComment(comment);

            comments.add(comment);
            commentAdapter.notifyItemInserted(comments.size());
        });
    }



    private void playSound(String soundUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(soundUrl), "audio/*");
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return sounds.size();
    }
    public void updateSounds(List<Sound> newSounds) {
        sounds.clear(); // Clear old data
        sounds.addAll(newSounds); // Add new data
        notifyDataSetChanged(); // Notify adapter about data change
    }


    static class SoundViewHolder extends RecyclerView.ViewHolder {
        TextView soundName;
        TextView soundAuthor;
        RecyclerView recyclerView;
        EditText editText;
        Button button;
        Button like_button;

        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);
            soundName = itemView.findViewById(R.id.sound_name);
            soundAuthor = itemView.findViewById(R.id.sound_author);
            recyclerView = itemView.findViewById(R.id.comments_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            editText = itemView.findViewById(R.id.comment_input);
            button = itemView.findViewById(R.id.add_comment_button);
            like_button = itemView.findViewById(R.id.like_button);
        }
    }
}
