package ws;

public enum NotificationType {

    ORDER_STATUS_CHANGED("OrderStatusChanged"),
    ACTION_DISCOUNT_CREATED("ActionDiscountCreated"),
    ORDER_ADDRESS_CHANGED("OrderAddressChanged"),
    PRODUCT_PRICE_CHANGED("ProductPriceChanged"),
    ONE_PRODUCT_LEFT("OneProductLeft");

    private final String name;

    NotificationType(final String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
}
