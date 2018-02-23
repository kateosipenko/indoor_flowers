package com.indoor.flowers.model;

public class NotificationWithTarget {

    private Notification notification;
    private Object target;

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
