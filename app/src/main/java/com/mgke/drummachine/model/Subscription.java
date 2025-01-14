package com.mgke.drummachine.model;

import java.util.Date;

public class Subscription {

    private String subscriberId;  // Идентификатор пользователя, который подписывается
    private String subscribedToId;  // Идентификатор пользователя, на которого подписан
    private Date subscriptionDate;  // Дата подписки

    // Конструктор по умолчанию
    public Subscription() {
    }

    // Конструктор с параметрами
    public Subscription(String subscriberId, String subscribedToId, Date subscriptionDate) {
        this.subscriberId = subscriberId;
        this.subscribedToId = subscribedToId;
        this.subscriptionDate = subscriptionDate;
    }

    // Геттеры и сеттеры
    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getSubscribedToId() {
        return subscribedToId;
    }

    public void setSubscribedToId(String subscribedToId) {
        this.subscribedToId = subscribedToId;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }
}
