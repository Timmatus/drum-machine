package com.mgke.drummachine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mgke.drummachine.model.Subscription;
import com.mgke.drummachine.repository.SubscriptionRepository;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionsActivity extends AppCompatActivity {

    private RecyclerView subscriptionsRecyclerView;
    private SubscriptionsAdapter subscriptionsAdapter;
    private SubscriptionRepository subscriptionRepository;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);

        subscriptionsRecyclerView = findViewById(R.id.subscriptions_recycler_view);
        subscriptionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        subscriptionRepository = new SubscriptionRepository();
        userId = getUserIdFromPreferences();

        subscriptionsAdapter = new SubscriptionsAdapter(new ArrayList<>(), this::unsubscribe, this::navigateToUserProfile);
        subscriptionsRecyclerView.setAdapter(subscriptionsAdapter);

        if (userId != null) {
            loadSubscriptions();
        }
    }

    private void loadSubscriptions() {
        subscriptionRepository.getSubscriptionsForUser(userId)
                .thenAccept(subscriptions -> runOnUiThread(() -> subscriptionsAdapter.updateSubscriptions(subscriptions)))
                .exceptionally(e -> {
                    runOnUiThread(() -> Toast.makeText(this, "Ошибка загрузки подписок: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    return null;
                });
    }

    private void unsubscribe(String subscribedToId) {
        subscriptionRepository.deleteSubscription(userId, subscribedToId)
                .thenAccept(aVoid -> runOnUiThread(() -> {
                    Toast.makeText(this, "Подписка отменена", Toast.LENGTH_SHORT).show();
                    loadSubscriptions();
                }))
                .exceptionally(e -> {
                    runOnUiThread(() -> Toast.makeText(this, "Ошибка при отмене подписки: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    return null;
                });
    }

    private void navigateToUserProfile(String subscribedToId) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("userId", subscribedToId);
        startActivity(intent);
    }

    private String getUserIdFromPreferences() {
        return getSharedPreferences("UserSession", MODE_PRIVATE).getString("userId", null);
    }
}
