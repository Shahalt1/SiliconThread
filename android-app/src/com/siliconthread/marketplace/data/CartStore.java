package com.siliconthread.marketplace.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CartStore {

    private static final String PREFS = "siliconthread.cart";
    private static final String KEY = "items";

    private static CartStore instance;

    private final SharedPreferences prefs;
    private final Map<String, Integer> items = new LinkedHashMap<>();
    private final List<Listener> listeners = new ArrayList<>();

    public interface Listener { void onCartChanged(); }

    public static synchronized CartStore get(Context ctx) {
        if (instance == null) instance = new CartStore(ctx.getApplicationContext());
        return instance;
    }

    private CartStore(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        try {
            String raw = prefs.getString(KEY, "[]");
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.optJSONObject(i);
                if (o == null) continue;
                items.put(o.optString("id"), o.optInt("qty", 1));
            }
        } catch (Exception ignored) {}
    }

    public void addListener(Listener l) { listeners.add(l); }
    public void removeListener(Listener l) { listeners.remove(l); }

    private void notifyChange() {
        for (Listener l : new ArrayList<>(listeners)) l.onCartChanged();
    }

    public void add(String id) { setQty(id, getQty(id) + 1); }

    public void setQty(String id, int qty) {
        if (qty <= 0) items.remove(id);
        else items.put(id, qty);
        persist();
        notifyChange();
    }

    public void remove(String id) {
        items.remove(id);
        persist();
        notifyChange();
    }

    public void clear() {
        items.clear();
        persist();
        notifyChange();
    }

    public int getQty(String id) {
        Integer q = items.get(id);
        return q == null ? 0 : q;
    }

    public int totalItems() {
        int n = 0;
        for (Integer q : items.values()) n += q;
        return n;
    }

    public boolean isEmpty() { return items.isEmpty(); }

    public Map<String, Integer> snapshot() {
        return new LinkedHashMap<>(items);
    }

    public double subtotal(ProductRepository repo) {
        double total = 0.0;
        for (Map.Entry<String, Integer> e : items.entrySet()) {
            Product p = repo.byId(e.getKey());
            if (p != null) total += p.price * e.getValue();
        }
        return total;
    }

    private void persist() {
        JSONArray arr = new JSONArray();
        for (Map.Entry<String, Integer> e : items.entrySet()) {
            try {
                JSONObject o = new JSONObject();
                o.put("id", e.getKey());
                o.put("qty", e.getValue());
                arr.put(o);
            } catch (Exception ignored) {}
        }
        prefs.edit().putString(KEY, arr.toString()).apply();
    }
}
