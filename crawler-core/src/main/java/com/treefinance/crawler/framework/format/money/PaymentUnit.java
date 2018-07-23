package com.treefinance.crawler.framework.format.money;

public enum PaymentUnit {
    IN("-"),
    OUT("");

    private final String value;

    PaymentUnit(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        switch (this) {
            case IN:
                return "payment.－";
            case OUT:
                return "payment.＋";
            default:
                return "unknown payment";
        }
    }
}
