package com.demo.transition.image.bus;


import com.demo.transition.image.ds.Image;
import com.demo.transition.image.transition.Thumbnail;

public final class ClickImageEvent {
	private final Image mImage;
	private final Thumbnail mThumbnail;

	public ClickImageEvent(Image image, Thumbnail thumbnail) {
		mImage = image;
		mThumbnail = thumbnail;
	}


	public Image getImage() {
		return mImage;
	}

	public Thumbnail getThumbnail() {
		return mThumbnail;
	}
}
