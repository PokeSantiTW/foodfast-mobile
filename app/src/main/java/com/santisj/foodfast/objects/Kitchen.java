package com.santisj.foodfast.objects;

import java.util.Arrays;
import java.util.List;

public class Kitchen {
    private String note;
    private int tableId;
    private List<Order> orders;

    public Kitchen(String note, int tableId, List<Order> orders) {
        this.note = note;
        this.tableId = tableId;
        this.orders = orders;
    }

    public Kitchen() {
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "Kitchen{" +
                "note='" + note + '\'' +
                ", tableId=" + tableId +
                ", orders=" + orders +
                '}';
    }
}
