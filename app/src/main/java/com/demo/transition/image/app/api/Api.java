package com.demo.transition.image.app.api;

import com.demo.transition.image.ds.ImagesRequest;
import com.demo.transition.image.ds.ImagesResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {
	@POST("/month_list")
	Call<ImagesResponse> getPhotoMonthList(@Body ImagesRequest req);


}