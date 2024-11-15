package com.mgke.drummachine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mgke.drummachine.model.Sound;
import java.util.List;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.SoundViewHolder> {

    private final List<Sound> sounds;
    private final Context context;

    public SoundAdapter(Context context, List<Sound> sounds) {
        this.context = context;
        this.sounds = sounds;
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

    static class SoundViewHolder extends RecyclerView.ViewHolder {
        TextView soundName;
        TextView soundAuthor;

        public SoundViewHolder(@NonNull View itemView) {
            super(itemView);
            soundName = itemView.findViewById(R.id.sound_name);
            soundAuthor = itemView.findViewById(R.id.sound_author);
        }
    }
}
