package com.demo.transition.image.ds;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public final class Image implements Serializable {
	@SerializedName("reqId")
	private String mReqId;
	@SerializedName("description")
	private String mDescription;
	@SerializedName("date")
	private Date mDate;
	@SerializedName("title")
	private String mTitle;
	@SerializedName("type")
	private String mType;
	@SerializedName("urls")
	private ImageUrl mImageUrl;

	public String getReqId() {
		return mReqId;
	}

	public String getDescription() {
		return mDescription;
	}

	public Date getDate() {
		return mDate;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getType() {
		return mType;
	}

	public ImageUrl getImageUrl() {
		return mImageUrl;
	}
}
