# SiliconThread

SiliconThread is a modern Android marketplace for high-performance hardware components including CPUs, GPUs, TPUs, and AI accelerators.

Built for gamers, PC enthusiasts, and AI developers, the platform combines cutting-edge compute hardware with a sleek gaming-inspired experience.

## Features

- **Home** — Featured GPUs, trending CPUs, AI accelerators, deals, and recommendations
- **Browse** — Category-based product catalog
- **Search** — Filter products by name, brand, and category
- **Wishlist** — Save items for later (in-memory store)
- **Cart** — Add/remove items, quantity controls, checkout flow
- **Profile** — Guest tester profile and app info
- **Product detail** — Specs, pricing, add to cart / buy now
- **Order confirmation** — Post-checkout summary screen

Product data (~200 hardware SKUs) ships in `assets/products.json` and is loaded at runtime by `ProductRepository`. No network calls are required for the catalog.

## Requirements

- **JDK 17+** (Java compilation)
- **Android SDK** with build-tools (default `36.0.0`) and platform `android-36.1`
- **`ANDROID_HOME`** pointing at your SDK install (e.g. `~/Library/Android/sdk`)
- **Debug keystore** at `~/.android/debug.keystore` (standard Android Studio debug key)

Min SDK **24**, target SDK **34**. Package: `com.siliconthread.marketplace`.

## Build & install

```bash
cd android-app
./build.sh
```

Output: `android-app/dist/SiliconThread-debug.apk`

Install on a connected device or emulator:

```bash
adb install -r android-app/dist/SiliconThread-debug.apk
```

## Project layout

```
SiliconThread/
├── README.md                 # This file
├── LICENSE                   # Project license
├── .gitignore                # Ignores build artifacts, *.class, android-app/build/, etc.
├── response.json             # Unrelated sample JSON (not used by the app)
│
└── android-app/              # Native Android app (no Gradle — SDK build-tools only)
    ├── AndroidManifest.xml   # App manifest, activities, SDK versions
    ├── build.sh              # Full APK build: aapt2 → javac → d8 → zipalign → apksigner
    ├── gen_products.js       # Regenerates assets/products.json (~200 products)
    ├── gen_icons.py          # Regenerates mipmap launcher PNGs (stdlib only)
    │
    ├── assets/
    │   └── products.json     # Catalog JSON bundled into the APK
    │
    ├── dist/                 # Build output (APK committed for convenience)
    │   ├── SiliconThread-debug.apk
    │   └── SiliconThread-debug.apk.idsig
    │
    ├── src/com/siliconthread/marketplace/
    │   ├── SplashActivity.java           # Launcher / splash screen
    │   ├── MainActivity.java             # Bottom-nav host (tabs)
    │   ├── ProductListActivity.java      # Category / listing screen
    │   ├── ProductDetailActivity.java    # Single product view
    │   ├── SearchActivity.java           # Full-screen search
    │   ├── OrderConfirmationActivity.java
    │   │
    │   ├── data/
    │   │   ├── Product.java              # Product model
    │   │   ├── ProductRepository.java    # Loads & queries products.json
    │   │   ├── CartStore.java            # In-memory cart
    │   │   └── WishlistStore.java        # In-memory wishlist
    │   │
    │   └── ui/
    │       ├── HomeFragment.java
    │       ├── CategoriesFragment.java
    │       ├── SearchFragment.java
    │       ├── WishlistFragment.java
    │       ├── CartFragment.java
    │       ├── ProfileFragment.java
    │       └── ProductCardBinder.java    # Shared product card binding
    │
    └── res/
        ├── layout/                       # Activities, fragments, list item XML
        │   ├── activity_splash.xml
        │   ├── activity_main.xml
        │   ├── activity_product_list.xml
        │   ├── activity_product_detail.xml
        │   ├── activity_order_confirmation.xml
        │   ├── fragment_home.xml
        │   ├── fragment_categories.xml
        │   ├── fragment_search.xml
        │   ├── fragment_wishlist.xml
        │   ├── fragment_cart.xml
        │   ├── fragment_profile.xml
        │   ├── item_product.xml
        │   ├── item_product_horizontal.xml
        │   ├── item_category.xml
        │   ├── item_cart.xml
        │   └── item_order_line.xml
        │
        ├── drawable/                       # Neon / gaming UI shapes & badges
        │   ├── bg_gradient.xml
        │   ├── splash_background.xml
        │   ├── card_bg.xml, card_bg_alt.xml
        │   ├── btn_neon.xml, btn_ghost.xml
        │   ├── chip_bg.xml, chip_bg_active.xml, chip_selector.xml
        │   ├── search_bg.xml, tab_bg.xml
        │   ├── product_image_bg.xml, product_image_bg_alt.xml
        │   ├── badge_deal.xml, badge_stock.xml, badge_out.xml
        │   ├── divider_neon.xml, success_ring.xml
        │   └── ic_launcher_background.xml, ic_launcher_foreground.xml
        │
        ├── values/
        │   ├── colors.xml                  # Dark neon palette
        │   ├── strings.xml                 # Copy, tabs, actions
        │   └── styles.xml                  # AppTheme, splash theme
        │
        └── mipmap-*/                       # Launcher icons (mdpi–xxxhdpi)
            ├── ic_launcher.png
            ├── ic_launcher_round.png
            └── mipmap-anydpi-v26/
                ├── ic_launcher.xml
                └── ic_launcher_round.xml
```

### Generated / ignored paths

| Path | Notes |
|------|--------|
| `android-app/build/` | Intermediate compile output (gitignored) |
| `android-app/dist/*.apk` | Signed debug APK produced by `build.sh` |

## Regenerating data & icons

```bash
# Refresh product catalog (~200 items)
node android-app/gen_products.js

# Refresh launcher PNGs at all densities
python3 android-app/gen_icons.py
```

After regenerating assets, run `./build.sh` again to produce a new APK.

## Appium / UI automation

The app exposes stable `testId` values on product cards (e.g. `product-card-<slug>`) for Appium sessions. Example session capabilities from a successful run:

- `appPackage`: `com.siliconthread.marketplace`
- `appActivity`: `com.siliconthread.marketplace.SplashActivity`
- `automationName`: `UiAutomator2`

## License

See [LICENSE](LICENSE).
