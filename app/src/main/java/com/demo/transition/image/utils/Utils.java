package com.demo.transition.image.utils;


import android.content.Context;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.util.DisplayMetrics;
import android.view.Display;

public final class Utils {

	public static ScreenSize getScreenSize(Context cxt) {
		return getScreenSize(cxt, 0);
	}


	public static ScreenSize getScreenSize(Context cxt, int displayIndex) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		Display[] displays = DisplayManagerCompat.getInstance(cxt).getDisplays();
		Display display = displays[displayIndex];
		display.getMetrics(displaymetrics);
		return new ScreenSize(displaymetrics.widthPixels, displaymetrics.heightPixels);
	}

	/**
	 * Screen-size in pixels.
	 */
	public static class ScreenSize {
		public int Width;
		public int Height;

		public ScreenSize(int _width, int _height) {
			Width = _width;
			Height = _height;
		}
	}
}
