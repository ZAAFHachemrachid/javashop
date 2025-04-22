package com.example.java_shop.utils;

import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.java_shop.R;

public class ImageLoader {
    
    private static final RequestOptions DEFAULT_OPTIONS = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image);

    /**
     * Loads an image from a URL into an ImageView
     *
     * @param imageView The target ImageView
     * @param url The URL of the image to load
     */
    public static void loadImage(ImageView imageView, String url) {
        if (imageView == null) return;

        Glide.with(imageView.getContext())
                .load(url)
                .apply(DEFAULT_OPTIONS)
                .into(imageView);
    }

    /**
     * Loads an image from a URL into an ImageView with custom placeholder and error drawables
     *
     * @param imageView The target ImageView
     * @param url The URL of the image to load
     * @param placeholderResId The resource ID of the placeholder drawable
     * @param errorResId The resource ID of the error drawable
     */
    public static void loadImage(ImageView imageView, String url,
                               @DrawableRes int placeholderResId,
                               @DrawableRes int errorResId) {
        if (imageView == null) return;

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(placeholderResId)
                .error(errorResId);

        Glide.with(imageView.getContext())
                .load(url)
                .apply(options)
                .into(imageView);
    }

    /**
     * Loads a circular image from a URL into an ImageView
     *
     * @param imageView The target ImageView
     * @param url The URL of the image to load
     */
    public static void loadCircularImage(ImageView imageView, String url) {
        if (imageView == null) return;

        RequestOptions circleOptions = DEFAULT_OPTIONS.clone()
                .circleCrop();

        Glide.with(imageView.getContext())
                .load(url)
                .apply(circleOptions)
                .into(imageView);
    }

    /**
     * Preloads an image into the cache
     *
     * @param imageView Any ImageView to get the context from
     * @param url The URL of the image to preload
     */
    public static void preloadImage(ImageView imageView, String url) {
        if (imageView == null) return;

        Glide.with(imageView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .preload();
    }

    /**
     * Clears the image loading queue and cache for the given ImageView
     *
     * @param imageView The ImageView to clear
     */
    public static void clear(ImageView imageView) {
        if (imageView == null) return;

        Glide.with(imageView.getContext())
                .clear(imageView);
    }
}