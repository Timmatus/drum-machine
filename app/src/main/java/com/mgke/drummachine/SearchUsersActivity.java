package com.mgke.drummachine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mgke.drummachine.model.User;

import java.util.ArrayList;
import java.util.List;

public class SearchUsersActivity extends AppCompatActivity {

    private EditText searchInput;
    private RecyclerView usersRecyclerView;
    private UsersAdapter usersAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        searchInput = findViewById(R.id.search_input);
        usersRecyclerView = findViewById(R.id.users_recycler_view);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        usersAdapter = new UsersAdapter(new ArrayList<>(), user -> {
            Intent intent = new Intent(SearchUsersActivity.this, OtherUserProfileActivity.class);
            intent.putExtra("userId", user.getId());
            startActivity(intent);
        });
        usersRecyclerView.setAdapter(usersAdapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = charSequence.toString().trim();
                if (!query.isEmpty()) {
                    searchUsers(query);
                } else {
                    usersAdapter.updateUsers(new ArrayList<>());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

    }

    private void searchUsers(String username) {
        db.collection("users")
                .orderBy("username", Query.Direction.ASCENDING)
                .startAt(username)
                .endAt(username + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = queryDocumentSnapshots.toObjects(User.class);
                    usersAdapter.updateUsers(users);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Ошибка поиска: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}