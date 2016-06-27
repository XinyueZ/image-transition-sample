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
import com.demo.transition.image.app.adapters.ImagesResponseAdapter;
import com.demo.transition.image.ds.ImagesResponse;

public final class ImageViewBindingHelper {

	@BindingAdapter({ "imageUrl"  })
	public static void setImageLoader(ImageView imageView, String imageUrl ) {
		Glide.with(App.Instance)
		     .load(imageUrl)
		     .dontAnimate()
		     .diskCacheStrategy(DiskCacheStrategy.ALL)
		     .centerCrop()
		     .into(imageView);
	}




	@BindingAdapter({ "imageUrl",
	                  "isThumbnail" })
	public static void setImageLoader(ImageView imageView, String imageUrl, boolean isThumbnail) {
		AnimatedVectorDrawableCompat animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(App.Instance, R.drawable.ic_animated_download);
		imageView.setImageDrawable(animatedVectorDrawableCompat);
		AnimatedVectorDrawableCompat dr = (AnimatedVectorDrawableCompat) imageView.getDrawable();
		VectorDrawableCompat errorDrawableCompat = VectorDrawableCompat.create(App.Instance.getResources(), R.drawable.ic_panorama, null);
		dr.start();
		if (!isThumbnail) {
			Glide.with(App.Instance)
			     .load(imageUrl)
			     .placeholder(dr)
			     .dontAnimate()
			     .error(errorDrawableCompat)
			     .diskCacheStrategy(DiskCacheStrategy.ALL)
			     .into(imageView);
		} else {
			Glide.with(App.Instance)
			     .load(imageUrl)
			     .placeholder(dr)
			     .dontAnimate()
			     .error(errorDrawableCompat)
			     .diskCacheStrategy(DiskCacheStrategy.ALL)
			     .centerCrop()
			     .into(imageView);
		}
	}



	@BindingAdapter({ "imagesResponse" })
	public static void setRecyclerData(RecyclerView rv, ImagesResponse response) {
		rv.setAdapter(new ImagesResponseAdapter(response));
	}

	@BindingAdapter({ "decoration" })
	public static void setRecyclerDecoration(RecyclerView rv, RecyclerView.ItemDecoration decoration) {
		rv.addItemDecoration(decoration);
	}
}
