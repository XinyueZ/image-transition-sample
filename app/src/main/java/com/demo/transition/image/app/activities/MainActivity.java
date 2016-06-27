package com.demo.transition.image.app.activities;

import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.demo.transition.image.R;
import com.demo.transition.image.app.App;
import com.demo.transition.image.app.api.Api;
import com.demo.transition.image.bus.LoadImagesErrorEvent;
import com.demo.transition.image.bus.LoadImagesSuccessEvent;
import com.demo.transition.image.databinding.ActivityMainBinding;
import com.demo.transition.image.ds.ImagesRequest;
import com.demo.transition.image.ds.ImagesResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

	private static final int LAYOUT = R.layout.activity_main;
	private ActivityMainBinding mBinding;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link LoadImagesSuccessEvent}.
	 *
	 * @param e Event {@link LoadImagesSuccessEvent}.
	 */
	@Subscribe
	public void onEvent(LoadImagesSuccessEvent e) {
		mBinding.setImagesResponse(e.getImagesResponse());
	}

	/**
	 * Handler for {@link LoadImagesErrorEvent}.
	 *
	 * @param e Event {@link LoadImagesErrorEvent}.
	 */
	@Subscribe
	public void onEvent(LoadImagesErrorEvent e) {

	}

	//------------------------------------------------

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getDefault()
		        .register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EventBus.getDefault()
		        .unregister(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = DataBindingUtil.setContentView(this, LAYOUT);
		mBinding.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		mBinding.setDividerDecoration(new DividerDecoration());

		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int currentMonth = calendar.get(Calendar.MONTH);
		int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		int shownMonth = currentMonth + 1;
		String timeZone = calendar.getTimeZone()
		                          .getID();

		loadPhotoList(year, shownMonth, timeZone);
		if (currentDay < 15) {
			loadPhotoList(year, currentMonth, timeZone);
		}
	}


	private void loadPhotoList(int year, int month, String timeZone) {
		ImagesRequest req = new ImagesRequest();
		req.setReqId(UUID.randomUUID()
		                 .toString());
		req.setYear(year);
		req.setMonth(month);
		req.setTimeZone(timeZone);

		Api api = App.Retrofit.create(Api.class);
		Call<ImagesResponse> call = api.getPhotoMonthList(req);
		call.enqueue(new Callback<ImagesResponse>() {
			@Override
			public void onResponse(Call<ImagesResponse> call, Response<ImagesResponse> response) {
				if (response.isSuccessful()) {
					EventBus.getDefault()
					        .post(new LoadImagesSuccessEvent(response.body()));
				} else {
					EventBus.getDefault()
					        .post(new LoadImagesErrorEvent());
				}
			}

			@Override
			public void onFailure(Call<ImagesResponse> call, Throwable t) {
				EventBus.getDefault()
				        .post(new LoadImagesErrorEvent());
			}
		});
	}


	public static class DividerDecoration extends RecyclerView.ItemDecoration {
		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			super.getItemOffsets(outRect, view, parent, state);
			if (!shouldDrawDividerAbove(view, parent)) {
				outRect.top = App.Instance.getResources()
				                          .getDimensionPixelOffset(R.dimen.divide_height);
			}
		}

		private boolean shouldDrawDividerAbove(View view, RecyclerView parent) {
			final RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
			return holder.getAdapterPosition() == 0;
		}
	}
}
