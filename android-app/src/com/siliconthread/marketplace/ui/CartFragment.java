package com.siliconthread.marketplace.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.siliconthread.marketplace.R;
import com.siliconthread.marketplace.data.CartStore;
import com.siliconthread.marketplace.data.Product;
import com.siliconthread.marketplace.data.ProductRepository;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartFragment extends Fragment {

    private static final NumberFormat USD = NumberFormat.getCurrencyInstance(Locale.US);

    private final List<String> ids = new ArrayList<>();
    private BaseAdapter adapter;
    private TextView empty, subtotal, taxView, totalView;
    private View summary;
    private CartStore store;

    private final CartStore.Listener listener = new CartStore.Listener() {
        @Override public void onCartChanged() { refresh(); }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        store = CartStore.get(getActivity());
        empty = (TextView) root.findViewById(R.id.cart_empty);
        subtotal = (TextView) root.findViewById(R.id.cart_subtotal);
        taxView = (TextView) root.findViewById(R.id.cart_tax);
        totalView = (TextView) root.findViewById(R.id.cart_total);
        summary = root.findViewById(R.id.cart_summary);

        ListView list = (ListView) root.findViewById(R.id.cart_list);
        final ProductRepository repo = ProductRepository.get(getActivity());
        adapter = new BaseAdapter() {
            @Override public int getCount() { return ids.size(); }
            @Override public Object getItem(int i) { return ids.get(i); }
            @Override public long getItemId(int i) { return i; }
            @Override public View getView(int i, View convertView, ViewGroup parent) {
                LayoutInflater li = LayoutInflater.from(parent.getContext());
                View v = convertView != null ? convertView : li.inflate(R.layout.item_cart, parent, false);
                final String id = ids.get(i);
                final Product p = repo.byId(id);
                if (p == null) return v;
                ((TextView) v.findViewById(R.id.cart_item_brand)).setText(p.brand.toUpperCase(Locale.US));
                ((TextView) v.findViewById(R.id.cart_item_name)).setText(p.model);
                ((TextView) v.findViewById(R.id.cart_item_price)).setText(USD.format(p.price));
                ((TextView) v.findViewById(R.id.cart_qty)).setText(String.valueOf(store.getQty(id)));
                v.setContentDescription("cart-item-" + id);
                v.findViewById(R.id.cart_qty_dec).setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) { store.setQty(id, Math.max(0, store.getQty(id) - 1)); }
                });
                v.findViewById(R.id.cart_qty_inc).setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) { store.setQty(id, store.getQty(id) + 1); }
                });
                v.findViewById(R.id.cart_remove).setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) { store.remove(id); }
                });
                v.findViewById(R.id.cart_item_image).setBackgroundResource(
                    Math.abs(id.hashCode()) % 2 == 0 ? R.drawable.product_image_bg : R.drawable.product_image_bg_alt);
                return v;
            }
        };
        list.setAdapter(adapter);

        Button checkout = (Button) root.findViewById(R.id.cart_checkout);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (store.isEmpty()) return;
                store.clear();
                Toast.makeText(getActivity(), "Order placed — thanks for testing!", Toast.LENGTH_SHORT).show();
            }
        });
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
        ids.clear();
        Map<String, Integer> snap = store.snapshot();
        for (String id : snap.keySet()) ids.add(id);
        ProductRepository repo = ProductRepository.get(getActivity());
        double sub = store.subtotal(repo);
        double tax = sub * 0.0875;
        double total = sub + tax;
        subtotal.setText(USD.format(sub));
        taxView.setText(USD.format(tax));
        totalView.setText(USD.format(total));
        summary.setVisibility(ids.isEmpty() ? View.GONE : View.VISIBLE);
        empty.setVisibility(ids.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }
}
