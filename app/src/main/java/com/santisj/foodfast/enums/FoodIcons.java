package com.santisj.foodfast.enums;

import com.santisj.foodfast.R;

public enum FoodIcons {
    BEER("foodicon_beer"),
    CAFE("foodicon_cafe"),
    CAKE("foodicon_cake"),
    CROISSANT("foodicon_croissant"),
    DRINK("foodicon_drink"),
    FISH("foodicon_fish"),
    HAMBURGER("foodicon_hamburger"),
    ICECREAM("foodicon_icecream"),
    LIQUOR("foodicon_liquor"),
    PIZZA("foodicon_pizza"),
    RAMEN("foodicon_ramen");

    public final String drawable;

    FoodIcons(String drawable) {
        this.drawable = drawable;
    }

    public String getDrawable() {
        return drawable;
    }
}
