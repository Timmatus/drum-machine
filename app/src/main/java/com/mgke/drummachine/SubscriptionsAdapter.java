package com.mgke.drummachine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mgke.drummachine.model.Subscription;

import java.util.List;

public class SubscriptionsAdapter extends RecyclerView.Adapter<SubscriptionsAdapter.SubscriptionViewHolder> {

    private List<Subscription> subscriptions;
    private final OnUnsubscribeClickListener onUnsubscribeClickListener;
    private final OnUserClickListener onUserClickListener;

    public interface OnUnsubscribeClickListener {
        void onUnsubscribeClick(String subscribedToId);
    }

    public interface OnUserClickListener {
        void onUserClick(String subscribedToId);
    }

    public SubscriptionsAdapter(List<Subscription> subscriptions,
                                OnUnsubscribeClickListener onUnsubscribeClickListener,
                                OnUserClickListener onUserClickListener) {
        this.subscriptions = subscriptions;
        this.onUnsubscribeClickListener = onUnsubscribeClickListener;
        this.onUserClickListener = onUserClickListener;
    }

    public void updateSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription, parent, false);
        return new SubscriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionViewHolder holder, int position) {
        Subscription subscription = subscriptions.get(position);

        holder.subscriptionText.setText("Подписка на: " + subscription.getSubscribedToId());
        holder.unsubscribeButton.setOnClickListener(v -> onUnsubscribeClickListener.onUnsubscribeClick(subscription.getSubscribedToId()));
        holder.itemView.setOnClickListener(v -> onUserClickListener.onUserClick(subscription.getSubscribedToId()));
    }

    @Override
    public int getItemCount() {
        return subscriptions.size();
    }

    static class SubscriptionViewHolder extends RecyclerView.ViewHolder {
        TextView subscriptionText;
        Button unsubscribeButton;

        public SubscriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            subscriptionText = itemView.findViewById(R.id.subscription_text);
            unsubscribeButton = itemView.findViewById(R.id.unsubscribe_button);
        }
    }
}
