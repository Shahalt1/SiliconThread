package com.siliconthread.marketplace.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.siliconthread.marketplace.MainActivity;
import com.siliconthread.marketplace.ProductListActivity;
import com.siliconthread.marketplace.R;
import com.siliconthread.marketplace.data.Product;
import com.siliconthread.marketplace.data.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        root.setContentDescription("home-screen");
        ProductRepository repo = ProductRepository.get(getActivity());

        fillRow(inflater, (LinearLayout) root.findViewById(R.id.row_featured_container),
                pickFeatured(repo.byCategory("GPU"), 10));
        fillRow(inflater, (LinearLayout) root.findViewById(R.id.row_trending_container),
                pickFeatured(repo.byCategory("CPU"), 10));
        List<Product> ai = new ArrayList<>(repo.byCategory("AI Accelerator"));
        ai.addAll(repo.byCategory("TPU"));
        fillRow(inflater, (LinearLayout) root.findViewById(R.id.row_ai_container),
                pickFeatured(ai, 10));
        fillRow(inflater, (LinearLayout) root.findViewById(R.id.row_deals_container),
                repo.byTag("deal"));
        List<Product> reco = new ArrayList<>();
        for (Product p : repo.all()) {
            if (p.hasTag("featured") || p.hasTag("flagship")) reco.add(p);
            if (reco.size() >= 12) break;
        }
        fillRow(inflater, (LinearLayout) root.findViewById(R.id.row_reco_container), reco);

        View hero = root.findViewById(R.id.home_hero);
        hero.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent i = new Intent(getActivity(), ProductListActivity.class);
                i.putExtra("category", "GPU");
                i.putExtra("title", "Featured GPUs");
                startActivity(i);
            }
        });

        View search = root.findViewById(R.id.home_open_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ((MainActivity) getActivity()).selectTab(MainActivity.TAB_SEARCH);
            }
        });

        View profile = root.findViewById(R.id.home_open_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ((MainActivity) getActivity()).selectTab(MainActivity.TAB_PROFILE);
            }
        });

        return root;
    }

    private static List<Product> pickFeatured(List<Product> source, int max) {
        List<Product> out = new ArrayList<>();
        for (Product p : source) {
            if (p.hasTag("featured") || p.hasTag("flagship")) out.add(p);
            if (out.size() >= max) return out;
        }
        // pad with remaining if not enough
        for (Product p : source) {
            if (!out.contains(p)) out.add(p);
            if (out.size() >= max) break;
        }
        return out;
    }

    private static void fillRow(LayoutInflater inflater, LinearLayout container, List<Product> products) {
        if (container == null) return;
        container.removeAllViews();
        for (Product p : products) {
            View card = inflater.inflate(R.layout.item_product_horizontal, container, false);
            ProductCardBinder.bind(card, p);
            container.addView(card);
        }
    }
}
