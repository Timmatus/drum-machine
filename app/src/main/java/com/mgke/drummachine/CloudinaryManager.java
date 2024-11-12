package com.mgke.drummachine;

import com.cloudinary.Cloudinary;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager {

    private static Cloudinary cloudinary;

    public static void initCloudinary() {
        if (cloudinary == null) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dcurxk2r2");
            config.put("api_key", "325634591651939");
            config.put("api_secret", "i1A8Yzw9wTwTUdjm8DKyHorXWg4");

            cloudinary = new Cloudinary(config);
        }
    }

    public static Cloudinary getCloudinary() {
        if (cloudinary == null) {
            initCloudinary();
        }
        return cloudinary;
    }
}