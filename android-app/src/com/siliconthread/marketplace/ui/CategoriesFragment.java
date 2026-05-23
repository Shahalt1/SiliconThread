package com.siliconthread.marketplace.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.siliconthread.marketplace.ProductListActivity;
import com.siliconthread.marketplace.R;
import com.siliconthread.marketplace.data.ProductRepository;

import java.util.List;

public class CategoriesFragment extends Fragment {

    private static final String[] ICONS = new String[]{"◢", "❒", "◉", "△", "▣", "◬", "◇", "◆"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_categories, container, false);
        final ProductRepository repo = ProductRepository.get(getActivity());
        final List<String> cats = repo.categories();
        GridView grid = (GridView) root.findViewById(R.id.category_grid);
        grid.setAdapter(new BaseAdapter() {
            @Override public int getCount() { return cats.size(); }
            @Override public Object getItem(int i) { return cats.get(i); }
            @Override public long getItemId(int i) { return i; }
            @Override public View getView(int i, View convertView, ViewGroup parent) {
                LayoutInflater li = LayoutInflater.from(parent.getContext());
                View v = convertView != null ? convertView : li.inflate(R.layout.item_category, parent, false);
                final String cat = cats.get(i);
                TextView name = (TextView) v.findViewById(R.id.category_name);
                TextView count = (TextView) v.findViewById(R.id.category_count);
                TextView icon = (TextView) v.findViewById(R.id.category_icon);
                name.setText(cat);
                int n = repo.byCategory(cat).size();
                count.setText(n + " products");
                icon.setText(ICONS[i % ICONS.length]);
                v.setContentDescription("category-card-" + cat.toLowerCase().replace(' ', '-'));
                v.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        Context ctx = view.getContext();
                        Intent it = new Intent(ctx, ProductListActivity.class);
                        it.putExtra("category", cat);
                        it.putExtra("title", cat);
                        ctx.startActivity(it);
                    }
                });
                return v;
            }
        });
        return root;
    }
}
