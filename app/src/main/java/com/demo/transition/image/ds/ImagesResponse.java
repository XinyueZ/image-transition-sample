package com.demo.transition.image.ds;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class ImagesResponse {

	@SerializedName("status")
	private int    mStatus;
	@SerializedName("reqId")
	private String mReqId;
	@SerializedName("result")
	private List<Image> mResult;


	public int getStatus() {
		return mStatus;
	}

	public String getReqId() {
		return mReqId;
	}

	public List<Image> getResult() {
		return mResult;
	}
}
