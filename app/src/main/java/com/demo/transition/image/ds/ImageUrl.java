package com.demo.transition.image.ds;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public final class ImageUrl implements Serializable{

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
