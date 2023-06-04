package com.santisj.foodfast.objects;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.Serializable;
import java.lang.reflect.Type;

public class FoodItem implements Serializable {
    private String name;
    private String description;
    private String image;
    private double price;
    private boolean isAvailable;

    private String itemId;

    public FoodItem() {
    }

    public FoodItem(String name, String description, String image, double price, boolean isAvailable) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.isAvailable = isAvailable;
        this.itemId = null;
    }

    public FoodItem(String name, String description, String image, double price, boolean isAvailable, String itemId) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.isAvailable = isAvailable;
        this.itemId = itemId;
    }

    public FoodItem(String name, String description, double price, boolean isAvailable) {
        this.name = name;
        this.description = description;
        this.image = "X";
        this.price = price;
        this.isAvailable = isAvailable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean available) {
        isAvailable = available;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "FoodItem{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", price=" + price +
                ", isAvailable=" + isAvailable +
                ", itemId='" + itemId + '\'' +
                '}';
    }
}
