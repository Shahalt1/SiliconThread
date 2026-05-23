package com.siliconthread.marketplace.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class WishlistStore {

    private static final String PREFS = "siliconthread.wishlist";
    private static final String KEY = "ids";

    private static WishlistStore instance;

    private final SharedPreferences prefs;
    private final Set<String> ids = new LinkedHashSet<>();
    private final List<Listener> listeners = new ArrayList<>();

    public interface Listener { void onWishlistChanged(); }

    public static synchronized WishlistStore get(Context ctx) {
        if (instance == null) instance = new WishlistStore(ctx.getApplicationContext());
        return instance;
    }

    private WishlistStore(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Set<String> stored = prefs.getStringSet(KEY, null);
        if (stored != null) ids.addAll(stored);
    }

    public void addListener(Listener l) { listeners.add(l); }
    public void removeListener(Listener l) { listeners.remove(l); }

    public boolean contains(String id) { return ids.contains(id); }

    public boolean toggle(String id) {
        boolean now;
        if (ids.contains(id)) { ids.remove(id); now = false; }
        else { ids.add(id); now = true; }
        persist();
        notifyChange();
        return now;
    }

    public void remove(String id) {
        if (ids.remove(id)) {
            persist();
            notifyChange();
        }
    }

    public List<String> all() { return new ArrayList<>(ids); }

    public boolean isEmpty() { return ids.isEmpty(); }

    public int size() { return ids.size(); }

    private void persist() {
        // SharedPreferences StringSet does not preserve insertion order, but we keep memory order.
        prefs.edit().putStringSet(KEY, Collections.unmodifiableSet(new LinkedHashSet<>(ids))).apply();
    }

    private void notifyChange() {
        for (Listener l : new ArrayList<>(listeners)) l.onWishlistChanged();
    }
}
