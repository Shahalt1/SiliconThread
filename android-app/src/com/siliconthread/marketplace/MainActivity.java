package com.siliconthread.marketplace;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.siliconthread.marketplace.data.CartStore;
import com.siliconthread.marketplace.data.WishlistStore;
import com.siliconthread.marketplace.ui.CartFragment;
import com.siliconthread.marketplace.ui.CategoriesFragment;
import com.siliconthread.marketplace.ui.HomeFragment;
import com.siliconthread.marketplace.ui.ProfileFragment;
import com.siliconthread.marketplace.ui.SearchFragment;
import com.siliconthread.marketplace.ui.WishlistFragment;

public class MainActivity extends Activity {

    public static final String EXTRA_OPEN_TAB = "open_tab";
    public static final int TAB_HOME = 0;
    public static final int TAB_CATEGORIES = 1;
    public static final int TAB_SEARCH = 2;
    public static final int TAB_WISHLIST = 3;
    public static final int TAB_CART = 4;
    public static final int TAB_PROFILE = 5;

    private final int[] tabIds = new int[]{
        R.id.tab_home, R.id.tab_categories, R.id.tab_search, R.id.tab_wishlist, R.id.tab_cart
    };

    private int currentTab = -1;

    private CartStore cartStore;
    private WishlistStore wishlistStore;
    private final CartStore.Listener cartListener = new CartStore.Listener() {
        @Override public void onCartChanged() { updateBadges(); }
    };
    private final WishlistStore.Listener wishListener = new WishlistStore.Listener() {
        @Override public void onWishlistChanged() { updateBadges(); }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cartStore = CartStore.get(this);
        wishlistStore = WishlistStore.get(this);
        cartStore.addListener(cartListener);
        wishlistStore.addListener(wishListener);

        View.OnClickListener tabClick = new View.OnClickListener() {
            @Override public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.tab_home) selectTab(TAB_HOME);
                else if (id == R.id.tab_categories) selectTab(TAB_CATEGORIES);
                else if (id == R.id.tab_search) selectTab(TAB_SEARCH);
                else if (id == R.id.tab_wishlist) selectTab(TAB_WISHLIST);
                else if (id == R.id.tab_cart) selectTab(TAB_CART);
            }
        };
        for (int id : tabIds) findViewById(id).setOnClickListener(tabClick);

        int initial = getIntent().getIntExtra(EXTRA_OPEN_TAB, TAB_HOME);
        selectTab(initial);
        updateBadges();
    }

    @Override
    protected void onDestroy() {
        cartStore.removeListener(cartListener);
        wishlistStore.removeListener(wishListener);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBadges();
    }

    public void selectTab(int tab) {
        if (tab == currentTab) return;
        currentTab = tab;
        Fragment f;
        switch (tab) {
            case TAB_CATEGORIES: f = new CategoriesFragment(); break;
            case TAB_SEARCH: f = new SearchFragment(); break;
            case TAB_WISHLIST: f = new WishlistFragment(); break;
            case TAB_CART: f = new CartFragment(); break;
            case TAB_PROFILE: f = new ProfileFragment(); break;
            case TAB_HOME:
            default: f = new HomeFragment(); break;
        }
        FragmentManager fm = getFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        tx.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        tx.replace(R.id.main_container, f, "tab-" + tab);
        tx.commitAllowingStateLoss();
        highlightTab(tab);
    }

    private void highlightTab(int tab) {
        int activeCol = 0xFF22D3EE;
        int inactiveCol = 0xFFA9B4D6;
        LinearLayout home = (LinearLayout) findViewById(R.id.tab_home);
        LinearLayout cats = (LinearLayout) findViewById(R.id.tab_categories);
        LinearLayout search = (LinearLayout) findViewById(R.id.tab_search);
        View wl = findViewById(R.id.tab_wishlist);
        View cart = findViewById(R.id.tab_cart);
        setTabState(home, tab == TAB_HOME, activeCol, inactiveCol);
        setTabState(cats, tab == TAB_CATEGORIES, activeCol, inactiveCol);
        setTabState(search, tab == TAB_SEARCH, activeCol, inactiveCol);
        setTabStateFrame(wl, tab == TAB_WISHLIST, activeCol, inactiveCol);
        setTabStateFrame(cart, tab == TAB_CART, activeCol, inactiveCol);
    }

    private void setTabState(LinearLayout tab, boolean active, int activeCol, int inactiveCol) {
        if (tab == null) return;
        int color = active ? activeCol : inactiveCol;
        for (int i = 0; i < tab.getChildCount(); i++) {
            View c = tab.getChildAt(i);
            if (c instanceof TextView) ((TextView) c).setTextColor(color);
        }
    }

    private void setTabStateFrame(View root, boolean active, int activeCol, int inactiveCol) {
        if (root == null) return;
        int color = active ? activeCol : inactiveCol;
        View ll = ((android.view.ViewGroup) root).getChildAt(0);
        if (ll instanceof LinearLayout) setTabState((LinearLayout) ll, active, activeCol, inactiveCol);
    }

    private void updateBadges() {
        TextView wb = (TextView) findViewById(R.id.badge_wishlist);
        TextView cb = (TextView) findViewById(R.id.badge_cart);
        int w = wishlistStore.size();
        int c = cartStore.totalItems();
        if (wb != null) {
            if (w > 0) { wb.setText(String.valueOf(w)); wb.setVisibility(View.VISIBLE); }
            else wb.setVisibility(View.GONE);
        }
        if (cb != null) {
            if (c > 0) { cb.setText(String.valueOf(c)); cb.setVisibility(View.VISIBLE); }
            else cb.setVisibility(View.GONE);
        }
    }
}
