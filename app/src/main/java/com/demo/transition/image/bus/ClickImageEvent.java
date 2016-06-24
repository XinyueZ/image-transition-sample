package com.demo.transition.image.bus;


import com.demo.transition.image.ds.Image;

public final class ClickImageEvent {
	private final Image mImage;

	public ClickImageEvent(Image image) {
		mImage = image;
	}


	public Image getImage() {
		return mImage;
	}
}
