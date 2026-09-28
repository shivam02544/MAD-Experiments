package com.prabhat.mad_expriment;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AdaptiveListActivity extends AppCompatActivity {

    private ImageView ivSelectedIcon;
    private FrameLayout selectedIconContainer;
    private TextView tvSelectedTitle;
    private TextView tvSelectedCategory;
    private TextView tvSelectedPrice;
    private TextView tvSelectedDescription;

    private List<AdaptiveItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adaptive_list);

        // Bind XML views
        ivSelectedIcon = findViewById(R.id.ivSelectedIcon);
        selectedIconContainer = findViewById(R.id.selectedIconContainer);
        tvSelectedTitle = findViewById(R.id.tvSelectedTitle);
        tvSelectedCategory = findViewById(R.id.tvSelectedCategory);
        tvSelectedPrice = findViewById(R.id.tvSelectedPrice);
        tvSelectedDescription = findViewById(R.id.tvSelectedDescription);
        ListView lvAdaptiveItems = findViewById(R.id.lvAdaptiveItems);

        // Populate sample data
        initData();

        // Setup custom adapter
        AdaptiveListAdapter adapter = new AdaptiveListAdapter(this, R.layout.list_item_adaptive, itemList);
        lvAdaptiveItems.setAdapter(adapter);

        // Set default preview to first item
        if (!itemList.isEmpty()) {
            updateSelectedPreview(itemList.get(0));
        }

        // Handle item click to adaptively update UI preview
        lvAdaptiveItems.setOnItemClickListener((parent, view, position, id) -> {
            AdaptiveItem selectedItem = itemList.get(position);
            updateSelectedPreview(selectedItem);
            Toast.makeText(AdaptiveListActivity.this, "Selected: " + selectedItem.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    private void initData() {
        itemList = new ArrayList<>();
        itemList.add(new AdaptiveItem(
                "Smartphone Pro Max",
                "Mobile Devices",
                "High-performance smartphone with OLED display, triple camera system, and all-day battery life.",
                "$999",
                R.drawable.ic_smartphone,
                R.drawable.bg_icon_blue
        ));
        itemList.add(new AdaptiveItem(
                "Ultra Tablet 11\"",
                "Computing",
                "Lightweight tablet with high-refresh rate screen, stylus support for creation and productivity.",
                "$799",
                R.drawable.ic_tablet,
                R.drawable.bg_icon_green
        ));
        itemList.add(new AdaptiveItem(
                "WorkStation Laptop",
                "Laptops & PCs",
                "Powerful workstation laptop with high-speed processor, 32GB RAM, and dedicated graphics card.",
                "$1,499",
                R.drawable.ic_laptop,
                R.drawable.bg_icon_orange
        ));
        itemList.add(new AdaptiveItem(
                "Fitness Smartwatch",
                "Wearables",
                "Health and fitness smartwatch featuring continuous heart rate, SpO2 sensor, and GPS tracking.",
                "$299",
                R.drawable.ic_smartwatch,
                R.drawable.bg_icon_purple
        ));
        itemList.add(new AdaptiveItem(
                "Wireless Headphones",
                "Audio Devices",
                "Premium active noise-canceling over-ear headphones with 40 hours of continuous playback.",
                "$199",
                R.drawable.ic_headphones,
                R.drawable.bg_icon_red
        ));
        itemList.add(new AdaptiveItem(
                "Smart OLED TV 55\"",
                "Home Entertainment",
                "4K HDR Smart OLED TV featuring vibrant colors, Dolby Vision audio, and integrated gaming mode.",
                "$1,199",
                R.drawable.ic_tv,
                R.drawable.bg_icon_blue
        ));
    }

    private void updateSelectedPreview(AdaptiveItem item) {
        tvSelectedTitle.setText(item.getTitle());
        tvSelectedCategory.setText(getString(R.string.category_format, item.getCategory()));
        tvSelectedPrice.setText(item.getPrice());
        tvSelectedDescription.setText(item.getDescription());
        ivSelectedIcon.setImageResource(item.getImageResId());
        if (item.getBgDrawableResId() != 0) {
            selectedIconContainer.setBackgroundResource(item.getBgDrawableResId());
        }
    }
}