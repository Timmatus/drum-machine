package com.mgke.drummachine;

import android.content.Context;
import android.net.Uri;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CloudinaryUploadSound {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Context context;

    public CloudinaryUploadSound(Context context) {
        this.context = context;
    }

    public void uploadSound(Uri soundUri, String soundName, UploadCallback callback) {
        executorService.submit(() -> {
            try {
                Map<String, Object> uploadParams = new HashMap<>();
                uploadParams.put("resource_type", "video");
                uploadParams.put("public_id", "sounds/" + soundName);

                InputStream inputStream = context.getContentResolver().openInputStream(soundUri);
                if (inputStream != null) {
                    Map<String, Object> uploadResult = CloudinaryManager.getCloudinary()
                            .uploader()
                            .upload(inputStream, uploadParams);
                    inputStream.close();

                    String soundUrl = (String) uploadResult.get("secure_url");
                    callback.onSuccess(soundUrl);
                } else {
                    callback.onFailure("Не удалось открыть звуковой файл");
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Ошибка загрузки звука: " + e.getMessage());
            }
        });
    }

    public interface UploadCallback {
        void onSuccess(String soundUrl);
        void onFailure(String error);
    }
}
