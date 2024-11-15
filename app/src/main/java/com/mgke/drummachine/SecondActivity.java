package com.mgke.drummachine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mgke.drummachine.model.Sound;
import com.mgke.drummachine.repository.SoundRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import java.util.List;

public class SecondActivity extends AppCompatActivity {

    // Новый объект MediaRecorder
    private MediaRecorder mediaRecorder;
    private String recordingFilePath;

    // Добавляем флаг для отслеживания паузы
    private boolean isRecordingPaused = false;

    public MediaPlayer[] mediaPlayers;
    public ButtonGridAdapter buttonGridAdapter;
    private SoundButtonGridAdapter soundButtonAdapter;
    private Handler handler = new Handler();
    private int currentButtonIndex = 0;
    private boolean isPlaying = true;
    private int bpm = 180; // Начальное значение BPM
    private Runnable highlightRunnable;
    private SoundRepository soundRepository;
    private CloudinaryUploadSound cloudinaryUploadSound;
    private Uri soundUri;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        requestPermissions();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        soundRepository = new SoundRepository();
        cloudinaryUploadSound = new CloudinaryUploadSound(this);

        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
//      userId = sharedPreferences.getString("userID", "default_user_id");
        userId = getUserIdFromPreferences();

        // Инициализация массива MediaPlayer
        mediaPlayers = new MediaPlayer[6];

        // Инициализация GridView
        GridView gridView1 = findViewById(R.id.gridView1);
        GridView gridView2 = findViewById(R.id.gridView2);

        // Установка адаптера для первого GridView (для кнопок)
        buttonGridAdapter = new ButtonGridAdapter(this, 32);
        gridView1.setAdapter(buttonGridAdapter);

        // Установка адаптера для второго GridView (для звуков)
        soundButtonAdapter = new SoundButtonGridAdapter(this, mediaPlayers);
        gridView2.setAdapter(soundButtonAdapter);

        // Установка первого звука как выбранного
        soundButtonAdapter.setSelectedSoundIndex(0);

        // Настройка HorizontalScrollView для выбора BPM
        LinearLayout layoutBpmValues = findViewById(R.id.layout_bpm_values);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int bpmValue = 105; bpmValue <= 250; bpmValue += 5) {
            final int currentBpmValue = bpmValue; // Делаем переменную финальной
            View bpmView = inflater.inflate(R.layout.item_bpm, layoutBpmValues, false);
            TextView bpmTextView = bpmView.findViewById(R.id.text_bpm_value);
            bpmTextView.setText(currentBpmValue + " BPM");

            // Установка выделения для текущего BPM
            if (currentBpmValue == bpm) {
                bpmTextView.setBackgroundResource(R.drawable.bpm_selected_background); // Выделенный фон
            }

            bpmTextView.setOnClickListener(v -> {
                // Получаем текущий индекс элемента
                int currentIndex = layoutBpmValues.indexOfChild(bpmView);

                // Сброс выделения для всех элементов, кроме выбранного
                resetBpmHighlight(layoutBpmValues, currentIndex);

                // Установка нового значения BPM
                bpm = currentBpmValue;
            });

            layoutBpmValues.addView(bpmView);
        }

        // Настройка ритма
        highlightRunnable = new Runnable() {
            @Override
            public void run() {
                if (isPlaying) {
                    // Сброс выделения всех кнопок
                    buttonGridAdapter.highlightButton(-1);

                    // Подсветка текущей кнопки
                    buttonGridAdapter.highlightButton(currentButtonIndex);

                    // Проход по всем звукам и воспроизведение активных кнопок
                    for (int soundIndex = 0; soundIndex < mediaPlayers.length; soundIndex++) {
                        List<Integer> activeButtons = soundButtonAdapter.getActiveButtonsForSound(soundIndex);
                        if (activeButtons.contains(currentButtonIndex)) {
                            playSound(soundIndex);
                        }
                    }

                    // Воспроизведение звука для активной кнопки в основном адаптере
                    List<Integer> activeButtons = buttonGridAdapter.getActiveButtons();
                    if (activeButtons.contains(currentButtonIndex)) {
                        playSound(soundButtonAdapter.getSelectedSoundIndex());
                    }

                    // Переход к следующей кнопке
                    currentButtonIndex = (currentButtonIndex + 1) % 32;

                    // Установка задержки перед следующим перебором
                    handler.postDelayed(this, getInterval());
                }
            }
        };

        handler.post(highlightRunnable);
    }

    private String getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.getString("userId", null);
    }

    private void uploadSoundToCloudinary(Uri soundUri, String soundName) {
        cloudinaryUploadSound.uploadSound(soundUri, soundName, new CloudinaryUploadSound.UploadCallback() {
            @Override
            public void onSuccess(String soundUrl) {
                saveSoundToFirestore(soundUrl, soundName);
            }

            @Override
            public void onFailure(String error) {
                // Обработка ошибки
                Toast.makeText(SecondActivity.this, "Ошибка загрузки: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSoundToFirestore(String soundUrl, String soundName) {
        Sound sound = new Sound();
        sound.setSoundName(soundName);
        sound.setSoundUrl(soundUrl);
        sound.setUserID(userId);

        soundRepository.saveSound(sound).thenAccept(success -> {
            if (success) {
                Toast.makeText(this, "Звук успешно сохранен!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Не удалось сохранить звук", Toast.LENGTH_SHORT).show();
            }
        }).exceptionally(e -> {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        });
    }

    // Обработка события выбора файла для загрузки
    public void onChooseSoundFileClick() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("audio/*");
        startActivityForResult(intent, 1001); // 1001 - код запроса для выбора файла
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            soundUri = data.getData(); // Получаем URI выбранного звукового файла
            uploadSoundToCloudinary(soundUri, "new_sound_name"); // Загрузка на Cloudinary
        }
    }


    private void resetBpmHighlight(LinearLayout layoutBpmValues, int selectedIndex) {
        for (int i = 0; i < layoutBpmValues.getChildCount(); i++) {
            View child = layoutBpmValues.getChildAt(i);
            TextView bpmTextView = child.findViewById(R.id.text_bpm_value);

            // Убираем выделение, если индекс не равен выбранному
            if (i != selectedIndex) {
                bpmTextView.setBackgroundResource(0); // Убираем выделение
            } else {
                bpmTextView.setBackgroundResource(R.drawable.bpm_selected_background); // Подсвечиваем выбранный элемент
            }
        }
    }

    private long getInterval() {
        return 60000 / bpm; // Интервал в миллисекундах
    }

    private void playSound(int soundIndex) {
        // Если уже существует MediaPlayer для данной кнопки, остановите и освободите его
        if (mediaPlayers[soundIndex] != null) {
            mediaPlayers[soundIndex].stop();
            mediaPlayers[soundIndex].release();
        }

        // Создание нового объекта MediaPlayer
        mediaPlayers[soundIndex] = MediaPlayer.create(this, getSoundResource(soundIndex));
        mediaPlayers[soundIndex].setOnCompletionListener(mp -> {
            mp.release();
            mediaPlayers[soundIndex] = null;
        });
        mediaPlayers[soundIndex].start();
    }

    private int getSoundResource(int index) {
        switch (index) {
            case 0:
                return R.raw.sound1;
            case 1:
                return R.raw.sound2;
            case 2:
                return R.raw.sound3;
            case 3:
                return R.raw.sound4;
            case 4:
                return R.raw.sound5;
            case 5:
                return R.raw.sound6;
            default:
                return -1;
        }
    }

    private void startRecording() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }

        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            recordingFilePath = getExternalCacheDir().getAbsolutePath() + "/" + UUID.randomUUID().toString() + ".mp4";
            mediaRecorder.setOutputFile(recordingFilePath);

            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecordingPaused = false;
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Recording failed", Toast.LENGTH_SHORT).show();
        }
    }


    // Метод для паузы записи
    private void pauseRecording() {
        if (mediaRecorder != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!isRecordingPaused) {
                mediaRecorder.pause();
                isRecordingPaused = true;
                Toast.makeText(this, "Recording paused", Toast.LENGTH_SHORT).show();
            } else {
                mediaRecorder.resume();
                isRecordingPaused = false;
                Toast.makeText(this, "Recording resumed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Pause not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecordingPaused = false;
            if (recordingFilePath != null) {
                File recordedFile = new File(recordingFilePath);
                if (recordedFile.exists()) {
                    Uri soundUri = Uri.fromFile(recordedFile);
                    showNameInputDialog(soundUri);
                    Toast.makeText(this, "Recording saved and uploading...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Recorded file does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Recording file path is null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showNameInputDialog(Uri soundUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите название мелодии для сохранения");

        // Создаем EditText для ввода имени звука
        final EditText input = new EditText(this);
        input.setHint("Название мелодии");
        builder.setView(input);

        // Кнопка "Сохранить"
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String soundName = input.getText().toString().trim();
            if (!soundName.isEmpty()) {
                uploadSoundToCloudinary(soundUri, soundName);
                Toast.makeText(this, "Recording saved and uploading...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Имя не может быть пустым", Toast.LENGTH_SHORT).show();
            }
        });

        // Кнопка "Отмена"
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        // Показать диалоговое окно
        builder.show();
    }


    // Запрос разрешений на запись звука
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1000);
        }
    }

    // Обработка результата запроса разрешений
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(highlightRunnable); // Остановка цикла
        // Освобождение ресурсов всех MediaPlayer
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }

        for (MediaPlayer mediaPlayer : mediaPlayers) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Инфляция меню из XML
        getMenuInflater().inflate(R.menu.menu_second_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            // Переход на MainActivity
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_record) {
            startRecording();
            return true;
        } else if (id == R.id.action_pause) {
            pauseRecording();
            return true;
        } else if (id == R.id.action_stop) {
            stopRecording();
            return true;
        } else if (id == R.id.action_clear) {
            // Очистка активных кнопок в GridView1
            buttonGridAdapter.setActiveButtons(new ArrayList<>()); // Установка пустого списка
            Toast.makeText(this, "Все кнопки очищены", Toast.LENGTH_SHORT).show();
            return true;
        }

        // В случае, если пункт меню не был обработан, передаем управление в родительский метод
        return super.onOptionsItemSelected(item);
    }
}