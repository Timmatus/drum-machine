package com.mgke.drummachine.repository;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.drummachine.MainActivity;
import com.mgke.drummachine.R;

public class LoginActivity extends AppCompatActivity {

    public UserRepository userRepository;
    public EditText emailEditText, passwordEditText;
    public TextView errorTextView;
    private boolean isPasswordVisible = false;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    public EditText editNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userRepository = new UserRepository(db.collection("users"));

        emailEditText = findViewById(R.id.email_et);
        passwordEditText = findViewById(R.id.password_et);
        editNickName = findViewById(R.id.username_et);

    }
}