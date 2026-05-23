package com.siliconthread.marketplace.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.siliconthread.marketplace.R;
import com.siliconthread.marketplace.data.Product;
import com.siliconthread.marketplace.data.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String[] SUGGESTIONS = new String[]{
        "RTX 5090", "Ryzen 9", "Coral", "DDR5", "NVMe", "AI Accelerator",
        "Motherboard", "Keyboard", "OLED", "Mouse"
    };

    private List<Product> results = new ArrayList<>();
    private BaseAdapter adapter;
    private TextView status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        final ProductRepository repo = ProductRepository.get(getActivity());
        final EditText input = (EditText) root.findViewById(R.id.search_input);
        final View clear = root.findViewById(R.id.search_clear);
        final ListView list = (ListView) root.findViewById(R.id.search_results);
        status = (TextView) root.findViewById(R.id.search_status);

        LinearLayout chips = (LinearLayout) root.findViewById(R.id.search_chips);
        for (final String sug : SUGGESTIONS) {
            TextView chip = new TextView(getActivity());
            chip.setText(sug);
            chip.setTextColor(0xFFE6F0FF);
            chip.setTextSize(12f);
            chip.setBackgroundResource(R.drawable.chip_bg);
            int pad = dp(10);
            chip.setPadding(pad, dp(6), pad, dp(6));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.rightMargin = dp(8);
            chip.setLayoutParams(lp);
            chip.setContentDescription("chip-" + sug.toLowerCase().replace(' ', '-'));
            chip.setClickable(true);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    input.setText(sug);
                    input.setSelection(sug.length());
                }
            });
            chips.addView(chip);
        }

        adapter = new BaseAdapter() {
            @Override public int getCount() { return results.size(); }
            @Override public Object getItem(int i) { return results.get(i); }
            @Override public long getItemId(int i) { return i; }
            @Override public View getView(int i, View convertView, ViewGroup parent) {
                LayoutInflater li = LayoutInflater.from(parent.getContext());
                View v = convertView != null ? convertView : li.inflate(R.layout.item_product, parent, false);
                ProductCardBinder.bind(v, results.get(i));
                return v;
            }
        };
        list.setAdapter(adapter);

        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String q = s.toString();
                clear.setVisibility(q.isEmpty() ? View.GONE : View.VISIBLE);
                results.clear();
                if (q.trim().length() > 0) {
                    results.addAll(repo.search(q));
                    status.setText(results.size() + " matches for \"" + q + "\"");
                } else {
                    status.setText("Try: RTX 5090, Ryzen, Coral, DDR5…");
                }
                adapter.notifyDataSetChanged();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { input.setText(""); }
        });

        return root;
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
