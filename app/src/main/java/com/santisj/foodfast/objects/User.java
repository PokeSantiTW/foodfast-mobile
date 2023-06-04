package com.santisj.foodfast.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private int tableId;
    private String name;
    private Map<String, Order> orderMap;

    public User(int tableId) {
        this.tableId = tableId;
        this.name = "Guest";
        this.orderMap = new HashMap<>();
    }

    public User() {
    }

    public User(int tableId, String name) {
        this.tableId = tableId;
        this.name = name;
        this.orderMap = new HashMap<>();
    }

    public User(int tableId, String name, Map<String, Order> map) {
        this.tableId = tableId;
        this.name = name;
        this.orderMap = map;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Order> getOrderMap() {
        return orderMap;
    }

    public void setOrderMap(Map<String, Order> orderMap) {
        this.orderMap = orderMap;
    }
}
