package com.demo.transition.image.bus;


import com.demo.transition.image.ds.ImagesResponse;

public final class LoadImagesSuccessEvent {

	private final ImagesResponse mImagesResponse;

	public LoadImagesSuccessEvent(ImagesResponse body) {

		mImagesResponse = body;
	}


	public ImagesResponse getImagesResponse() {
		return mImagesResponse;
	}
}
