package com.demo.transition.image.bus;


import android.widget.ImageView;

import com.demo.transition.image.ds.Image;
import com.demo.transition.image.transition.Thumbnail;

import java.lang.ref.WeakReference;

public final class ClickImageEvent {
	private final Image mImage;
	private final Thumbnail mThumbnail;
	private final WeakReference<ImageView> mSharedImageView;

	public ClickImageEvent(Image image, Thumbnail thumbnail, ImageView imageView) {
		mImage = image;
		mThumbnail = thumbnail;
		mSharedImageView = new WeakReference<>(imageView);
	}


	public Image getImage() {
		return mImage;
	}

	public Thumbnail getThumbnail() {
		return mThumbnail;
	}


	public ImageView getSharedImageView() {
		return mSharedImageView.get();
	}


	public WeakReference<ImageView>  getSharedImageViewWeakRef() {
		return mSharedImageView;
	}

}
