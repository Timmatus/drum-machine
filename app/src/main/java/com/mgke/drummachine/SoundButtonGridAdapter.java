package com.mgke.drummachine;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class SoundButtonGridAdapter extends BaseAdapter {
    private Context context;
    private MediaPlayer[] mediaPlayers;
    private int selectedSoundIndex = 0; // Устанавливаем индекс по умолчанию на 0
    private SparseArray<List<Integer>> activeButtonStates; // Состояния активных кнопок для каждого звука

    public SoundButtonGridAdapter(Context context, MediaPlayer[] mediaPlayers) {
        this.context = context;
        this.mediaPlayers = mediaPlayers;
        this.activeButtonStates = new SparseArray<>();
    }

    @Override
    public int getCount() {
        return mediaPlayers.length; // Количество кнопок со звуками
    }

    @Override
    public Object getItem(int position) {
        return null; // Не требуется
    }

    @Override
    public long getItemId(int position) {
        return position; // Идентификатор позиции
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button button;
        if (convertView == null) {
            button = new Button(context);
            button.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
        } else {
            button = (Button) convertView;
        }

        button.setText("Sound " + (position + 1)); // Установка текста кнопки

        // Установка фона для выделенного состояния
        if (position == selectedSoundIndex) {
            button.setBackgroundResource(R.drawable.button_selected_background); // Фон для выделенного звука
        } else {
            button.setBackgroundResource(R.drawable.button_default_background); // Фон по умолчанию
        }

        button.setOnClickListener(v -> {
            // Сохранить текущие активные кнопки перед переключением звука
            List<Integer> currentActiveButtons = ((SecondActivity) context).buttonGridAdapter.getActiveButtons();
            setActiveButtonsForSound(selectedSoundIndex, currentActiveButtons);

            selectedSoundIndex = position; // Обновляем индекс выбранного звука

            // Восстановить активные кнопки для выбранного звука
            List<Integer> activeButtonsForSelectedSound = getActiveButtonsForSound(position);
            ((SecondActivity) context).buttonGridAdapter.setActiveButtons(activeButtonsForSelectedSound);

            notifyDataSetChanged(); // Обновляем адаптер
        });

        return button;
    }

    public void setSelectedSoundIndex(int index) {
        selectedSoundIndex = index;
        notifyDataSetChanged();
    }

    // Метод для установки активных кнопок для конкретного звука
    public void setActiveButtonsForSound(int soundIndex, List<Integer> activeButtons) {
        activeButtonStates.put(soundIndex, new ArrayList<>(activeButtons));
    }

    // Метод для получения активных кнопок для конкретного звука
    public List<Integer> getActiveButtonsForSound(int soundIndex) {
        List<Integer> buttons = activeButtonStates.get(soundIndex);
        return buttons != null ? buttons : new ArrayList<>(); // Вернуть пустой список, если нет активных кнопок
    }

    public int getSelectedSoundIndex() {
        return selectedSoundIndex;
    }
}