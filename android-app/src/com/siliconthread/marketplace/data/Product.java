package com.siliconthread.marketplace.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Product {
    public final String id;
    public final String testId;
    public final String category;
    public final String subCategory;
    public final String brand;
    public final String model;
    public final double price;
    public final double originalPrice;
    public final String shortText;
    public final String description;
    public final double rating;
    public final int reviewCount;
    public final boolean inStock;
    public final String dealLabel;
    public final int discountPct;
    public final Map<String, String> specs;
    public final Map<String, String> benchmark;
    public final List<String> tags;
    public final String image;

    private Product(Builder b) {
        this.id = b.id;
        this.testId = b.testId;
        this.category = b.category;
        this.subCategory = b.subCategory;
        this.brand = b.brand;
        this.model = b.model;
        this.price = b.price;
        this.originalPrice = b.originalPrice;
        this.shortText = b.shortText;
        this.description = b.description;
        this.rating = b.rating;
        this.reviewCount = b.reviewCount;
        this.inStock = b.inStock;
        this.dealLabel = b.dealLabel;
        this.discountPct = b.discountPct;
        this.specs = b.specs;
        this.benchmark = b.benchmark;
        this.tags = b.tags;
        this.image = b.image;
    }

    public String displayName() {
        return brand + " " + model;
    }

    public boolean hasDeal() { return dealLabel != null && !dealLabel.isEmpty(); }

    public boolean hasTag(String tag) {
        if (tags == null) return false;
        for (String t : tags) if (t.equalsIgnoreCase(tag)) return true;
        return false;
    }

    public static Product fromJson(JSONObject o) {
        Builder b = new Builder();
        b.id = o.optString("id");
        b.testId = o.optString("testId");
        b.category = o.optString("category");
        b.subCategory = o.optString("subCategory", "");
        b.brand = o.optString("brand");
        b.model = o.optString("model");
        b.price = o.optDouble("price", 0.0);
        b.originalPrice = o.optDouble("originalPrice", 0.0);
        b.shortText = o.optString("short");
        b.description = o.optString("description");
        b.rating = o.optDouble("rating", 0.0);
        b.reviewCount = o.optInt("reviewCount", 0);
        b.inStock = o.optBoolean("inStock", true);
        b.image = o.optString("image", "");
        JSONObject deal = o.optJSONObject("deal");
        if (deal != null) {
            b.dealLabel = deal.optString("label");
            b.discountPct = deal.optInt("discountPct", 0);
        } else {
            b.dealLabel = "";
            b.discountPct = 0;
        }
        b.specs = toStringMap(o.optJSONObject("specs"));
        b.benchmark = toStringMap(o.optJSONObject("benchmark"));
        b.tags = new ArrayList<>();
        JSONArray tags = o.optJSONArray("tags");
        if (tags != null) {
            for (int i = 0; i < tags.length(); i++) {
                b.tags.add(tags.optString(i));
            }
        }
        return new Product(b);
    }

    private static Map<String, String> toStringMap(JSONObject o) {
        Map<String, String> map = new LinkedHashMap<>();
        if (o == null) return map;
        Iterator<String> it = o.keys();
        while (it.hasNext()) {
            String k = it.next();
            map.put(k, o.optString(k));
        }
        return map;
    }

    public static final class Builder {
        public String id, testId, category, subCategory, brand, model, shortText, description, dealLabel, image;
        public double price, originalPrice, rating;
        public int reviewCount, discountPct;
        public boolean inStock;
        public Map<String, String> specs;
        public Map<String, String> benchmark;
        public List<String> tags;
    }
}
