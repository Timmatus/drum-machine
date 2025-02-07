package com.mgke.drummachine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mgke.drummachine.model.Sound;

import java.util.List;

public class LikedSoundAdapter extends RecyclerView.Adapter<LikedSoundAdapter.LikedSoundViewHolder> {

    private final Context context;
    private final List<Sound> likedSounds;
    private final String userId;

    public LikedSoundAdapter(Context context, List<Sound> likedSounds, String userId) {
        this.context = context;
        this.likedSounds = likedSounds;
        this.userId = userId;
    }

    @Override
    public LikedSoundViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_liked_sound, parent, false);
        return new LikedSoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LikedSoundViewHolder holder, int position) {
        Sound sound = likedSounds.get(position);
        holder.soundName.setText(sound.getSoundName());
        holder.soundAuthor.setText("Автор: " + sound.getUserID());
    }

    @Override
    public int getItemCount() {
        return likedSounds.size();
    }

    static class LikedSoundViewHolder extends RecyclerView.ViewHolder {
        TextView soundName;
        TextView soundAuthor;

        public LikedSoundViewHolder(View itemView) {
            super(itemView);
            soundName = itemView.findViewById(R.id.sound_name);
            soundAuthor = itemView.findViewById(R.id.sound_author);
        }
    }
}
