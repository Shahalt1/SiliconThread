package com.siliconthread.marketplace;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.siliconthread.marketplace.data.Product;
import com.siliconthread.marketplace.data.ProductRepository;
import com.siliconthread.marketplace.ui.ProductCardBinder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ProductListActivity extends Activity {

    private String activeBrand = "All";
    private String activeSort = "default";
    private String category;

    private final List<Product> filtered = new ArrayList<>();
    private BaseAdapter adapter;
    private TextView countView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        category = getIntent().getStringExtra("category");
        String title = getIntent().getStringExtra("title");
        if (title == null) title = category != null ? category : "Catalog";

        ((TextView) findViewById(R.id.list_title)).setText(title);
        findViewById(R.id.list_back).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        countView = (TextView) findViewById(R.id.list_count);

        final ProductRepository repo = ProductRepository.get(this);
        final List<Product> source = category != null && !category.isEmpty()
            ? repo.byCategory(category)
            : new ArrayList<>(repo.all());

        // brand chips
        Set<String> brands = new LinkedHashSet<>();
        brands.add("All");
        for (Product p : source) brands.add(p.brand);
        LinearLayout filterRow = (LinearLayout) findViewById(R.id.filter_row);
        for (final String brand : brands) {
            final TextView chip = buildChip(brand, brand.equals("All"));
            chip.setContentDescription("filter-" + brand.toLowerCase().replace(' ', '-'));
            chip.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    activeBrand = brand;
                    for (int i = 0; i < filterRow.getChildCount(); i++) {
                        View c = filterRow.getChildAt(i);
                        c.setSelected(c == chip);
                    }
                    applyFilters(source);
                }
            });
            filterRow.addView(chip);
        }

        // sort chips
        final String[][] sorts = new String[][]{
            {"default", "Featured"}, {"price_asc", "Price ↑"}, {"price_desc", "Price ↓"},
            {"rating", "Rating"}, {"name", "Name"}
        };
        final LinearLayout sortRow = (LinearLayout) findViewById(R.id.sort_row);
        for (final String[] s : sorts) {
            final TextView chip = buildChip(s[1], s[0].equals("default"));
            chip.setContentDescription("sort-" + s[0]);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    activeSort = s[0];
                    for (int i = 0; i < sortRow.getChildCount(); i++) {
                        View c = sortRow.getChildAt(i);
                        c.setSelected(c == chip);
                    }
                    applyFilters(source);
                }
            });
            sortRow.addView(chip);
        }

        ListView list = (ListView) findViewById(R.id.list_products);
        adapter = new BaseAdapter() {
            @Override public int getCount() { return filtered.size(); }
            @Override public Object getItem(int i) { return filtered.get(i); }
            @Override public long getItemId(int i) { return i; }
            @Override public View getView(int i, View convertView, ViewGroup parent) {
                LayoutInflater li = LayoutInflater.from(parent.getContext());
                View v = convertView != null ? convertView : li.inflate(R.layout.item_product, parent, false);
                ProductCardBinder.bind(v, filtered.get(i));
                return v;
            }
        };
        list.setAdapter(adapter);

        applyFilters(source);
    }

    private TextView buildChip(String label, boolean active) {
        TextView chip = new TextView(this);
        chip.setText(label);
        chip.setTextSize(12f);
        chip.setTextColor(active ? 0xFF000000 : 0xFFE6F0FF);
        int pad = (int) (10 * getResources().getDisplayMetrics().density);
        chip.setPadding(pad, (int) (6 * getResources().getDisplayMetrics().density), pad,
            (int) (6 * getResources().getDisplayMetrics().density));
        chip.setBackgroundResource(R.drawable.chip_selector);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = (int) (8 * getResources().getDisplayMetrics().density);
        chip.setLayoutParams(lp);
        chip.setClickable(true);
        chip.setFocusable(true);
        chip.setSelected(active);
        return chip;
    }

    private void applyFilters(List<Product> source) {
        filtered.clear();
        for (Product p : source) {
            if (!activeBrand.equals("All") && !activeBrand.equalsIgnoreCase(p.brand)) continue;
            filtered.add(p);
        }
        ProductRepository.sortBy(filtered, activeSort);
        countView.setText(filtered.size() + " products");
        adapter.notifyDataSetChanged();
    }
}
