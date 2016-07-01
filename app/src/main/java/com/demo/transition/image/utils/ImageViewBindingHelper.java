package com.demo.transition.image.utils;


import android.databinding.BindingAdapter;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.demo.transition.image.R;
import com.demo.transition.image.app.App;

public final class ImageViewBindingHelper {

	@BindingAdapter({ "imageUrl"  })
	public static void setImageLoader(ImageView imageView, String imageUrl ) {
		AnimatedVectorDrawableCompat dr = (AnimatedVectorDrawableCompat) imageView.getDrawable();
		VectorDrawableCompat errorDrawableCompat = VectorDrawableCompat.create(App.Instance.getResources(), R.drawable.ic_panorama, null);
		Glide.with(App.Instance)
		     .load(imageUrl)
		     .crossFade()
		     .error(errorDrawableCompat)
		     .diskCacheStrategy(DiskCacheStrategy.ALL)
		     .centerCrop()
		     .into(imageView);
	}




	@BindingAdapter({ "thumbnailUrl"} )
	public static void setThumbnailLoader(ImageView imageView, String thumbnailUrl) {
		AnimatedVectorDrawableCompat animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(App.Instance, R.drawable.ic_animated_download);
		imageView.setImageDrawable(animatedVectorDrawableCompat);
		AnimatedVectorDrawableCompat dr = (AnimatedVectorDrawableCompat) imageView.getDrawable();
		VectorDrawableCompat errorDrawableCompat = VectorDrawableCompat.create(App.Instance.getResources(), R.drawable.ic_panorama, null);
		dr.start();

		Glide.with(App.Instance)
		     .load(thumbnailUrl)
		     .placeholder(dr)
		     .crossFade()
		     .dontAnimate()
		     .error(errorDrawableCompat)
		     .diskCacheStrategy(DiskCacheStrategy.ALL)
		     .into(imageView);
	}



	@BindingAdapter({ "decoration" })
	public static void setRecyclerDecoration(RecyclerView rv, RecyclerView.ItemDecoration decoration) {
		rv.addItemDecoration(decoration);
	}
}
