package com.mgke.drummachine;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class ButtonGridAdapter extends BaseAdapter {
    private Context context;
    private int buttonCount;
    private List<Integer> activeButtons; // Список активных кнопок
    private int highlightedIndex = -1; // Индекс выделенной кнопки

    public ButtonGridAdapter(Context context, int buttonCount) {
        this.context = context;
        this.buttonCount = buttonCount;
        this.activeButtons = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return buttonCount; // Количество кнопок
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

        // Установка фона в зависимости от состояния кнопки
        if (activeButtons.contains(position)) {
            button.setBackgroundResource(R.drawable.button_active_background); // Фон для активной кнопки
        } else {
            button.setBackgroundResource(R.drawable.button_inactive_background); // Фон по умолчанию
        }

        // Подсветка текущей кнопки
        if (position == highlightedIndex) {
            button.setBackgroundResource(R.drawable.button_highlight_background); // Фон для выделенной кнопки
        }

        button.setText(" "); // Установка текста кнопки

        button.setOnClickListener(v -> {
            if (activeButtons.contains(position)) {
                activeButtons.remove(Integer.valueOf(position)); // Удаляем из активных
            } else {
                activeButtons.add(position); // Добавляем в активные
            }

            notifyDataSetChanged(); // Обновляем адаптер
        });

        return button;
    }

    public List<Integer> getActiveButtons() {
        return activeButtons; // Возвращаем список активных кнопок
    }

    public void setActiveButtons(List<Integer> activeButtons) {
        this.activeButtons.clear();
        this.activeButtons.addAll(activeButtons);
        notifyDataSetChanged(); // Обновляем адаптер
    }

    public void highlightButton(int index) {
        this.highlightedIndex = index; // Устанавливаем индекс выделенной кнопки
        notifyDataSetChanged(); // Обновляем адаптер
    }
}