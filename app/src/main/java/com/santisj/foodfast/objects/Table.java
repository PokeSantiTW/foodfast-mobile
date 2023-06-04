package com.santisj.foodfast.objects;

import java.util.List;

public class Table {
    private String access;
    private boolean isTableAvailable;
    private List<Order> orders;

    public Table(String access, boolean isTableAvailable) {
        this.access = access;
        this.isTableAvailable = isTableAvailable;
        this.orders = null;
    }

    public Table(String access, boolean isTableAvailable, List<Order> orders) {
        this.access = access;
        this.isTableAvailable = isTableAvailable;
        this.orders = orders;
    }

    public Table() {
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public boolean isTableAvailable() {
        return isTableAvailable;
    }

    public void setTableAvailable(boolean tableAvailable) {
        isTableAvailable = tableAvailable;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "Table{" +
                "access='" + access + '\'' +
                ", isTableAvailable=" + isTableAvailable +
                '}';
    }
}
