package com.demo.transition.image.utils;


import android.databinding.BindingAdapter;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.demo.transition.image.R;
import com.demo.transition.image.app.App;

public final class ImageViewBindingHelper {
	@BindingAdapter({ "imageUrl" })
	public static void setImageLoader(ImageView imageView, String imageUrl) {
		AnimatedVectorDrawableCompat animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(App.Instance, R.drawable.ic_animated_download);
		imageView.setImageDrawable(animatedVectorDrawableCompat);
		AnimatedVectorDrawableCompat dr = (AnimatedVectorDrawableCompat) imageView.getDrawable();
		dr.start();
		Glide.with(App.Instance)
		     .load(imageUrl)
		     .placeholder(dr)
		     .dontAnimate()
		     .diskCacheStrategy(DiskCacheStrategy.ALL)
		     .into(imageView);
	}
}
