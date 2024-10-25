package com.mgke.drummachine;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer[] mediaPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Настройка Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Инициализация массива MediaPlayer
        mediaPlayers = new MediaPlayer[6];

        // Инициализация кнопок
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);
        Button button5 = findViewById(R.id.button5);
        Button button6 = findViewById(R.id.button6);

        // Установка слушателей для кнопок
        button1.setOnClickListener(v -> playSound(0, R.raw.sound1));
        button2.setOnClickListener(v -> playSound(1, R.raw.sound2));
        button3.setOnClickListener(v -> playSound(2, R.raw.sound3));
        button4.setOnClickListener(v -> playSound(3, R.raw.sound4));
        button5.setOnClickListener(v -> playSound(4, R.raw.sound5));
        button6.setOnClickListener(v -> playSound(5, R.raw.sound6));
    }

    private void playSound(int index, int soundResource) {
        // Если уже существует MediaPlayer для данной кнопки, остановите и освободите его
        if (mediaPlayers[index] != null) {
            mediaPlayers[index].stop();
            mediaPlayers[index].release();
        }

        // Создание нового объекта MediaPlayer
        mediaPlayers[index] = MediaPlayer.create(this, soundResource);
        mediaPlayers[index].setOnCompletionListener(mp -> {
            mp.release(); // Освобождение ресурсов после завершения
            mediaPlayers[index] = null; // Установить на null для дальнейшего использования
        });
        mediaPlayers[index].start(); // Запуск воспроизведения
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Освобождение ресурсов всех MediaPlayer
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Инфляция меню из XML
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            // Переход на SecondActivity
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}