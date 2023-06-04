package com.santisj.foodfast.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.santisj.foodfast.objects.FoodItem;

import java.lang.reflect.Type;

public class FoodItemSerializer implements JsonSerializer<FoodItem>, JsonDeserializer<FoodItem> {
    @Override
    public JsonElement serialize(FoodItem src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", src.getName());
        jsonObject.addProperty("description", src.getDescription());
        jsonObject.addProperty("image", src.getImage());
        jsonObject.addProperty("price", src.getPrice());
        jsonObject.addProperty("isAvailable", src.isIsAvailable());
        jsonObject.addProperty("itemId", src.getItemId());
        return jsonObject;
    }

    @Override
    public FoodItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        String image = "";
        try {
            image = jsonObject.get("image").getAsString();
        } catch (NullPointerException e) {
            image = null;
        }
        double price = jsonObject.get("price").getAsDouble();
        boolean isAvailable = jsonObject.get("isAvailable").getAsBoolean();
        String itemId = jsonObject.get("itemId").getAsString();
        return new FoodItem(name, description, image, price, isAvailable, itemId);
    }
}
