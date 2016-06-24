package com.demo.transition.image.ds;

import com.google.gson.annotations.SerializedName;

public final class ImagesRequest {
	@SerializedName("reqId")
	private String mReqId;
	@SerializedName("year")
	private int    mYear;
	@SerializedName("month")
	private int    mMonth;
	@SerializedName("timeZone")
	private String mTimeZone;

	public void setReqId(String reqId) {
		mReqId = reqId;
	}

	public void setYear(int year) {
		mYear = year;
	}

	public void setMonth(int month) {
		mMonth = month;
	}

	public void setTimeZone(String timeZone) {
		mTimeZone = timeZone;
	}

	public String getReqId() {
		return mReqId;
	}

	public int getYear() {
		return mYear;
	}

	public int getMonth() {
		return mMonth;
	}

	public String getTimeZone() {
		return mTimeZone;
	}
}
