package com.siliconthread.marketplace.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

import com.siliconthread.marketplace.ProductDetailActivity;
import com.siliconthread.marketplace.R;
import com.siliconthread.marketplace.data.Product;

import java.text.NumberFormat;
import java.util.Locale;

public final class ProductCardBinder {

    private static final NumberFormat USD = NumberFormat.getCurrencyInstance(Locale.US);

    private ProductCardBinder() {}

    public static void bind(View card, final Product p) {
        card.setContentDescription("product-card-" + p.id);
        card.setTag(p.testId);

        TextView name = (TextView) card.findViewById(R.id.product_name);
        TextView brand = (TextView) card.findViewById(R.id.product_brand);
        TextView priceView = (TextView) card.findViewById(R.id.product_price);
        TextView origPrice = (TextView) card.findViewById(R.id.product_original_price);
        TextView shortText = (TextView) card.findViewById(R.id.product_short);
        TextView rating = (TextView) card.findViewById(R.id.product_rating);
        TextView dealBadge = (TextView) card.findViewById(R.id.product_deal_badge);
        TextView stockBadge = (TextView) card.findViewById(R.id.product_stock_badge);
        View image = card.findViewById(R.id.product_image);

        if (brand != null) brand.setText(p.brand.toUpperCase(Locale.US));
        if (name != null) name.setText(p.model);
        if (priceView != null) priceView.setText(USD.format(p.price));
        if (origPrice != null) {
            if (p.originalPrice > 0 && p.originalPrice > p.price) {
                origPrice.setText(USD.format(p.originalPrice));
                origPrice.setPaintFlags(origPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                origPrice.setVisibility(View.VISIBLE);
            } else {
                origPrice.setVisibility(View.GONE);
            }
        }
        if (shortText != null) shortText.setText(p.shortText);
        if (rating != null) rating.setText("★ " + String.format(Locale.US, "%.1f", p.rating) + "  (" + p.reviewCount + ")");
        if (dealBadge != null) {
            if (p.hasDeal()) {
                dealBadge.setText(p.dealLabel);
                dealBadge.setVisibility(View.VISIBLE);
            } else {
                dealBadge.setVisibility(View.GONE);
            }
        }
        if (stockBadge != null) {
            if (p.inStock) {
                stockBadge.setText("IN STOCK");
                stockBadge.setBackgroundResource(R.drawable.badge_stock);
                stockBadge.setTextColor(0xFF38E89A);
            } else {
                stockBadge.setText("OUT");
                stockBadge.setBackgroundResource(R.drawable.badge_out);
                stockBadge.setTextColor(0xFFFF4D6D);
            }
        }
        if (image != null) {
            int alt = (Math.abs(p.id.hashCode()) % 2);
            image.setBackgroundResource(alt == 0 ? R.drawable.product_image_bg : R.drawable.product_image_bg_alt);
        }

        card.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Context ctx = v.getContext();
                Intent i = new Intent(ctx, ProductDetailActivity.class);
                i.putExtra("product_id", p.id);
                ctx.startActivity(i);
            }
        });
    }

    public static String formatPrice(double price) {
        return USD.format(price);
    }
}
