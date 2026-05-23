package com.siliconthread.marketplace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.siliconthread.marketplace.data.Product;
import com.siliconthread.marketplace.data.ProductRepository;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class OrderConfirmationActivity extends Activity {

    public static final String EXTRA_IDS = "order_ids";
    public static final String EXTRA_QTYS = "order_qtys";
    public static final String EXTRA_SUBTOTAL = "order_subtotal";
    public static final String EXTRA_ORDER_NUMBER = "order_number";

    private static final NumberFormat USD = NumberFormat.getCurrencyInstance(Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        String[] ids = getIntent().getStringArrayExtra(EXTRA_IDS);
        int[] qtys = getIntent().getIntArrayExtra(EXTRA_QTYS);
        double subtotal = getIntent().getDoubleExtra(EXTRA_SUBTOTAL, 0.0);
        String orderNumber = getIntent().getStringExtra(EXTRA_ORDER_NUMBER);
        if (ids == null || qtys == null || orderNumber == null) { finish(); return; }

        double tax = subtotal * 0.0875;
        double total = subtotal + tax;

        ((TextView) findViewById(R.id.order_number)).setText(orderNumber);
        ((TextView) findViewById(R.id.order_subtotal)).setText(USD.format(subtotal));
        ((TextView) findViewById(R.id.order_tax)).setText(USD.format(tax));
        ((TextView) findViewById(R.id.order_total)).setText(USD.format(total));

        Calendar eta = Calendar.getInstance();
        eta.add(Calendar.DAY_OF_MONTH, 3);
        SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d", Locale.US);
        ((TextView) findViewById(R.id.order_eta)).setText(fmt.format(eta.getTime()));

        ProductRepository repo = ProductRepository.get(this);
        LinearLayout itemsContainer = (LinearLayout) findViewById(R.id.order_items_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < ids.length; i++) {
            Product p = repo.byId(ids[i]);
            if (p == null) continue;
            int qty = qtys[i];
            View line = inflater.inflate(R.layout.item_order_line, itemsContainer, false);
            line.setContentDescription("order-line-" + p.id);
            ((TextView) line.findViewById(R.id.order_line_brand)).setText(p.brand.toUpperCase(Locale.US));
            ((TextView) line.findViewById(R.id.order_line_name)).setText(p.model);
            ((TextView) line.findViewById(R.id.order_line_qty)).setText("Qty " + qty + "  •  " + USD.format(p.price) + " each");
            ((TextView) line.findViewById(R.id.order_line_price)).setText(USD.format(p.price * qty));
            line.findViewById(R.id.order_line_image).setBackgroundResource(
                Math.abs(p.id.hashCode()) % 2 == 0 ? R.drawable.product_image_bg : R.drawable.product_image_bg_alt);
            itemsContainer.addView(line);
        }

        View.OnClickListener returnHome = new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent i = new Intent(OrderConfirmationActivity.this, MainActivity.class);
                i.putExtra(MainActivity.EXTRA_OPEN_TAB, MainActivity.TAB_HOME);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }
        };
        ((Button) findViewById(R.id.order_continue)).setOnClickListener(returnHome);
        ((Button) findViewById(R.id.order_view_orders)).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent i = new Intent(OrderConfirmationActivity.this, MainActivity.class);
                i.putExtra(MainActivity.EXTRA_OPEN_TAB, MainActivity.TAB_PROFILE);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(MainActivity.EXTRA_OPEN_TAB, MainActivity.TAB_HOME);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }
}
