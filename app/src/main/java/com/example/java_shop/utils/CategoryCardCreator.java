package com.example.java_shop.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.navigation.Navigation;
import com.example.java_shop.R;
import com.example.java_shop.data.models.Category;
import com.google.android.material.card.MaterialCardView;
import java.util.List;
import android.os.Bundle;

public class CategoryCardCreator {

    public static void createCategoryCards(Context context, LinearLayout container, List<Category> categories) {
        if (context == null || container == null || categories == null) {
            return;
        }
        
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(context);
        
        for (Category category : categories) {
            View cardView = inflater.inflate(R.layout.item_category, container, false);
            
            ImageView iconView = cardView.findViewById(R.id.category_image);
            TextView nameView = cardView.findViewById(R.id.category_name);
            
            // Set category data
            nameView.setText(category.getName());
            
            // Set icon based on category id
            int iconResId = getCategoryIcon(category.getId());
            iconView.setImageResource(iconResId);
            
            // Set click listener
            cardView.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("categoryId", category.getId());
                Navigation.findNavController(container)
                        .navigate(R.id.action_homeFragment_to_categoryDetailsFragment, args);
            });
            
            // Add margins
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            cardView.setLayoutParams(params);
            
            // Add to container
            container.addView(cardView);
        }
    }
    
    private static int getCategoryIcon(String categoryId) {
        switch (categoryId.toLowerCase()) {
            case "face":
                return R.drawable.ic_category_face;
            case "eye":
                return R.drawable.ic_category_eye;
            case "lip":
                return R.drawable.ic_category_lips;
            case "skincare":
                return R.drawable.ic_category_skincare;
            case "tools":
                return R.drawable.ic_category_tools;
            default:
                return R.drawable.baseline_category_24;
        }
    }
} 