package com.demo.transition.image.transition;

import java.io.Serializable;

/**
 * Size, location of a thumbnail.
 *
 * @author Xinyue Zhao
 */
public final class Thumbnail implements Serializable {
	private int mTop;
	private int mLeft;
	private int mWidth;
	private int mHeight;
	private String mSource;

	public Thumbnail(int top, int left, int width, int height ) {
		mTop = top;
		mLeft = left;
		mWidth = width;
		mHeight = height;
	}

	public void setSource(String source) {
		mSource = source;
	}

	public int getTop() {
		return mTop;
	}

	public int getLeft() {
		return mLeft;
	}

	public int getWidth() {
		return mWidth;
	}

	public int getHeight() {
		return mHeight;
	}


	String getSource() {
		return mSource;
	}
}
