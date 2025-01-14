package com.mgke.drummachine;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.drummachine.model.Comment;
import com.mgke.drummachine.model.Sound;
import com.mgke.drummachine.repository.CommentRepository;

import java.util.ArrayList;
import java.util.List;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.SoundViewHolder> {

    private final List<Sound> sounds;
    private final Context context;
    private final CommentRepository commentRepository;

    public SoundAdapter(Context context, List<Sound> sounds) {
        this.context = context;
        this.sounds = sounds;
        commentRepository = new CommentRepository();
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

        holder.soundName.setText(sound.getSoundName());
        holder.soundAuthor.setText("Автор: " + sound.getUserID());
        holder.itemView.setOnClickListener(v -> playSound(sound.getSoundUrl()));

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

        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);
            soundName = itemView.findViewById(R.id.sound_name);
            soundAuthor = itemView.findViewById(R.id.sound_author);
            recyclerView = itemView.findViewById(R.id.comments_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            editText = itemView.findViewById(R.id.comment_input);
            button = itemView.findViewById(R.id.add_comment_button);
        }
    }
}
