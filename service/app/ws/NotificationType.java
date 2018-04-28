package ws;

public enum NotificationType {

    ORDER_STATUS_CHANGED("OrderStatusChanged");

    private final String name;

    NotificationType(final String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
