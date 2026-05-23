package com.siliconthread.marketplace.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ProductRepository {

    private static final String TAG = "ProductRepo";
    private static ProductRepository instance;

    private final List<Product> all = new ArrayList<>();
    private final List<String> categories = new ArrayList<>();
    private final List<String> brands = new ArrayList<>();

    public static synchronized ProductRepository get(Context ctx) {
        if (instance == null) {
            instance = new ProductRepository();
            instance.load(ctx.getApplicationContext());
        }
        return instance;
    }

    private void load(Context ctx) {
        StringBuilder sb = new StringBuilder();
        AssetManager am = ctx.getAssets();
        try {
            InputStream is = am.open("products.json");
            BufferedReader r = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            char[] buf = new char[4096];
            int n;
            while ((n = r.read(buf)) > 0) sb.append(buf, 0, n);
            r.close();
        } catch (Exception e) {
            Log.e(TAG, "Failed to load products.json", e);
            return;
        }
        try {
            JSONObject root = new JSONObject(sb.toString());
            JSONArray arr = root.optJSONArray("products");
            if (arr == null) return;
            Set<String> catSet = new LinkedHashSet<>();
            Set<String> brandSet = new LinkedHashSet<>();
            for (int i = 0; i < arr.length(); i++) {
                Product p = Product.fromJson(arr.optJSONObject(i));
                all.add(p);
                if (p.category != null && !p.category.isEmpty()) catSet.add(p.category);
                if (p.brand != null && !p.brand.isEmpty()) brandSet.add(p.brand);
            }
            categories.addAll(catSet);
            brands.addAll(brandSet);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse products.json", e);
        }
    }

    public List<Product> all() { return Collections.unmodifiableList(all); }
    public List<String> categories() { return Collections.unmodifiableList(categories); }
    public List<String> brands() { return Collections.unmodifiableList(brands); }

    public Product byId(String id) {
        if (id == null) return null;
        for (Product p : all) if (id.equals(p.id)) return p;
        return null;
    }

    public List<Product> byCategory(String category) {
        List<Product> out = new ArrayList<>();
        for (Product p : all) if (p.category.equalsIgnoreCase(category)) out.add(p);
        return out;
    }

    public List<Product> byTag(String tag) {
        List<Product> out = new ArrayList<>();
        for (Product p : all) if (p.hasTag(tag)) out.add(p);
        return out;
    }

    public List<Product> search(String query) {
        List<Product> out = new ArrayList<>();
        if (query == null) return out;
        String q = query.trim().toLowerCase(Locale.US);
        if (q.isEmpty()) return out;
        for (Product p : all) {
            String hay = (p.displayName() + " " + p.category + " " + p.shortText + " " + p.description).toLowerCase(Locale.US);
            if (hay.contains(q)) out.add(p);
        }
        return out;
    }

    public static void sortBy(List<Product> list, String key) {
        Comparator<Product> cmp;
        switch (key == null ? "" : key) {
            case "price_asc":
                cmp = new Comparator<Product>() {
                    public int compare(Product a, Product b) { return Double.compare(a.price, b.price); }
                };
                break;
            case "price_desc":
                cmp = new Comparator<Product>() {
                    public int compare(Product a, Product b) { return Double.compare(b.price, a.price); }
                };
                break;
            case "rating":
                cmp = new Comparator<Product>() {
                    public int compare(Product a, Product b) { return Double.compare(b.rating, a.rating); }
                };
                break;
            case "name":
                cmp = new Comparator<Product>() {
                    public int compare(Product a, Product b) { return a.displayName().compareToIgnoreCase(b.displayName()); }
                };
                break;
            default:
                return;
        }
        Collections.sort(list, cmp);
    }
}
