package com.prabhat.mad_expriment;

public class AdaptiveItem {
    private final String title;
    private final String category;
    private final String description;
    private final String price;
    private final int imageResId;
    private final int bgDrawableResId;

    public AdaptiveItem(String title, String category, String description, String price, int imageResId, int bgDrawableResId) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.bgDrawableResId = bgDrawableResId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getBgDrawableResId() {
        return bgDrawableResId;
    }
}