package com.siliconthread.marketplace.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.siliconthread.marketplace.R;
import com.siliconthread.marketplace.data.Product;
import com.siliconthread.marketplace.data.ProductRepository;
import com.siliconthread.marketplace.data.WishlistStore;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {

    private final List<Product> items = new ArrayList<>();
    private BaseAdapter adapter;
    private TextView empty, count;
    private WishlistStore store;

    private final WishlistStore.Listener listener = new WishlistStore.Listener() {
        @Override public void onWishlistChanged() { refresh(); }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_wishlist, container, false);
        store = WishlistStore.get(getActivity());
        empty = (TextView) root.findViewById(R.id.wishlist_empty);
        count = (TextView) root.findViewById(R.id.wishlist_count);

        ListView list = (ListView) root.findViewById(R.id.wishlist_list);
        adapter = new BaseAdapter() {
            @Override public int getCount() { return items.size(); }
            @Override public Object getItem(int i) { return items.get(i); }
            @Override public long getItemId(int i) { return i; }
            @Override public View getView(int i, View convertView, ViewGroup parent) {
                LayoutInflater li = LayoutInflater.from(parent.getContext());
                View v = convertView != null ? convertView : li.inflate(R.layout.item_product, parent, false);
                ProductCardBinder.bind(v, items.get(i));
                return v;
            }
        };
        list.setAdapter(adapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        store.addListener(listener);
        refresh();
    }

    @Override
    public void onPause() {
        store.removeListener(listener);
        super.onPause();
    }

    private void refresh() {
        items.clear();
        ProductRepository repo = ProductRepository.get(getActivity());
        for (String id : store.all()) {
            Product p = repo.byId(id);
            if (p != null) items.add(p);
        }
        count.setText(items.size() + " ITEMS");
        empty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }
}
