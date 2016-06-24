package com.demo.transition.image.ds;


import com.google.gson.annotations.SerializedName;

public final class ImageUrl  {

	@SerializedName("normal")
	private String mNormal;

	@SerializedName("hd")
	private String mHd;


	public String getNormal() {
		return mNormal;
	}

	public String getHd() {
		return mHd;
	}
}
