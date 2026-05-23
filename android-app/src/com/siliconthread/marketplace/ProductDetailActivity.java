package com.siliconthread.marketplace;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.siliconthread.marketplace.data.CartStore;
import com.siliconthread.marketplace.data.Product;
import com.siliconthread.marketplace.data.ProductRepository;
import com.siliconthread.marketplace.data.WishlistStore;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class ProductDetailActivity extends Activity {

    private static final NumberFormat USD = NumberFormat.getCurrencyInstance(Locale.US);

    private Product product;
    private TextView wishBtn;
    private WishlistStore wishStore;
    private CartStore cartStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        String id = getIntent().getStringExtra("product_id");
        product = ProductRepository.get(this).byId(id);
        if (product == null) { finish(); return; }

        wishStore = WishlistStore.get(this);
        cartStore = CartStore.get(this);

        findViewById(R.id.detail_back).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        wishBtn = (TextView) findViewById(R.id.detail_wishlist);
        wishBtn.setContentDescription("wishlist-button");
        updateWishIcon();
        wishBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                boolean now = wishStore.toggle(product.id);
                updateWishIcon();
                Toast.makeText(ProductDetailActivity.this,
                    now ? "Added to wishlist" : "Removed from wishlist", Toast.LENGTH_SHORT).show();
            }
        });

        ((TextView) findViewById(R.id.detail_brand)).setText(product.brand.toUpperCase(Locale.US));
        ((TextView) findViewById(R.id.detail_name)).setText(product.model);
        ((TextView) findViewById(R.id.detail_short)).setText(product.shortText);
        ((TextView) findViewById(R.id.detail_price)).setText(USD.format(product.price));
        TextView orig = (TextView) findViewById(R.id.detail_original);
        if (product.originalPrice > 0 && product.originalPrice > product.price) {
            orig.setText(USD.format(product.originalPrice));
            orig.setPaintFlags(orig.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            orig.setVisibility(View.VISIBLE);
        }
        TextView deal = (TextView) findViewById(R.id.detail_deal);
        if (product.hasDeal()) {
            deal.setText(product.dealLabel);
            deal.setVisibility(View.VISIBLE);
        }
        ((TextView) findViewById(R.id.detail_rating))
            .setText("★ " + String.format(Locale.US, "%.1f", product.rating)
                + "  •  " + product.reviewCount + " reviews  •  "
                + (product.inStock ? "In Stock" : "Out of Stock"));
        ((TextView) findViewById(R.id.detail_description)).setText(product.description);

        LinearLayout specs = (LinearLayout) findViewById(R.id.detail_specs);
        for (Map.Entry<String, String> e : product.specs.entrySet()) {
            specs.addView(buildKV(e.getKey(), e.getValue()));
        }

        LinearLayout bench = (LinearLayout) findViewById(R.id.detail_benchmarks);
        if (product.benchmark == null || product.benchmark.isEmpty()) {
            findViewById(R.id.detail_benchmarks_label).setVisibility(View.GONE);
            bench.setVisibility(View.GONE);
        } else {
            for (Map.Entry<String, String> e : product.benchmark.entrySet()) {
                bench.addView(buildKV(e.getKey(), e.getValue()));
            }
        }

        buildCarousel();

        Button addCart = (Button) findViewById(R.id.detail_add_cart);
        Button buyNow = (Button) findViewById(R.id.detail_buy_now);
        addCart.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                cartStore.add(product.id);
                Toast.makeText(ProductDetailActivity.this,
                    product.displayName() + " added to cart", Toast.LENGTH_SHORT).show();
            }
        });
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                cartStore.add(product.id);
                Intent i = new Intent(ProductDetailActivity.this, MainActivity.class);
                i.putExtra(MainActivity.EXTRA_OPEN_TAB, MainActivity.TAB_CART);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    private void updateWishIcon() {
        boolean on = wishStore.contains(product.id);
        wishBtn.setText(on ? "♥" : "♡");
        wishBtn.setTextColor(on ? 0xFFFF3DAA : 0xFFA9B4D6);
    }

    private View buildKV(String key, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        int pad = dp(6);
        row.setPadding(0, pad, 0, pad);
        TextView k = new TextView(this);
        k.setText(key);
        k.setTextColor(0xFFA9B4D6);
        k.setTextSize(12f);
        k.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextColor(0xFFE6F0FF);
        v.setTextSize(12f);
        v.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f));
        row.addView(k);
        row.addView(v);
        return row;
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    private void buildCarousel() {
        final HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.detail_carousel);
        final LinearLayout container = (LinearLayout) findViewById(R.id.detail_carousel_container);
        final LinearLayout dots = (LinearLayout) findViewById(R.id.carousel_dots);
        final int slides = 4;
        for (int i = 0; i < slides; i++) {
            View slide = new View(this);
            slide.setBackgroundResource(i % 2 == 0 ? R.drawable.product_image_bg : R.drawable.product_image_bg_alt);
            slide.setContentDescription("detail-image-" + i);
            container.addView(slide);
            View dot = new View(this);
            dot.setBackgroundResource(R.drawable.chip_bg);
            LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(dp(8), dp(8));
            dlp.leftMargin = dp(4); dlp.rightMargin = dp(4);
            dot.setLayoutParams(dlp);
            dot.setContentDescription("carousel-dot-" + i);
            dots.addView(dot);
        }
        // Size each slide to screen width minus 32dp padding, set after layout.
        scroll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                scroll.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int slideW = scroll.getWidth() - dp(32);
                int slideH = scroll.getHeight();
                for (int i = 0; i < container.getChildCount(); i++) {
                    View slide = container.getChildAt(i);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(slideW, slideH);
                    lp.rightMargin = dp(10);
                    slide.setLayoutParams(lp);
                }
            }
        });
    }
}
