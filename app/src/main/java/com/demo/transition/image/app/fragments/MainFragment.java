package com.demo.transition.image.app.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.demo.transition.image.R;
import com.demo.transition.image.app.App;
import com.demo.transition.image.app.adapters.ImagesResponseAdapter;
import com.demo.transition.image.app.api.Api;
import com.demo.transition.image.bus.LoadImagesErrorEvent;
import com.demo.transition.image.bus.LoadImagesSuccessEvent;
import com.demo.transition.image.databinding.FragmentMainBinding;
import com.demo.transition.image.ds.Image;
import com.demo.transition.image.ds.ImagesRequest;
import com.demo.transition.image.ds.ImagesResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.demo.transition.image.app.App.DEFAULT_OPEN_DETAIL_ACTIVITY;
import static com.demo.transition.image.app.App.DEFAULT_USE_SUPPORT_TRANSITION;
import static com.demo.transition.image.app.App.DEFAULT_USE_SUPPORT_TRANSITION_SIMPLE;
import static com.demo.transition.image.app.App.Instance;

public final class MainFragment extends BaseFragment {
	private static final int LAYOUT = R.layout.fragment_main;
	private static final int MENU_MAIN = R.menu.menu_main;
	private FragmentMainBinding mBinding;
	private List<Image> mLastImagesList;

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
		if (e.getImagesResponse() != null) {
			setData(e.getImagesResponse()
			         .getResult());
		}
	}


	/**
	 * Handler for {@link LoadImagesErrorEvent}.
	 *
	 * @param e Event {@link LoadImagesErrorEvent}.
	 */
	@Subscribe
	public void onEvent(@SuppressWarnings("UnusedParameters") LoadImagesErrorEvent e) {

	}


	//------------------------------------------------
	public static Fragment newInstance() {
		return MainFragment.instantiate(Instance, MainFragment.class.getName());
	}


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mBinding = DataBindingUtil.inflate(inflater, LAYOUT, container, false);
		return mBinding.getRoot();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBinding.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
		mBinding.setDividerDecoration(new DividerDecoration());

		mBinding.activityMainSrl.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorBlack);
		mBinding.activityMainSrl.setProgressViewEndTarget(true, getActionBarHeight() * 2);
		mBinding.activityMainSrl.setProgressViewOffset(false, 0, getActionBarHeight() * 2);

		mBinding.activityMainSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				clearData();
				loadOrSetData();
			}
		});

		mBinding.toolbar.setTitle(R.string.app_name);
		mBinding.toolbar.setNavigationIcon(R.drawable.ic_close);
		mBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ActivityCompat.finishAfterTransition(getActivity());
			}
		});
		mBinding.toolbar.inflateMenu(MENU_MAIN);
		boolean currentOpenDetailActivity = App.Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
		                                                .getBoolean(App.KEY_OPEN_DETAIL_ACTIVITY, DEFAULT_OPEN_DETAIL_ACTIVITY);
		Menu menu = mBinding.toolbar.getMenu();
		if (currentOpenDetailActivity) {
			menu.findItem(R.id.action_detail_activity)
			    .setChecked(currentOpenDetailActivity);
		} else {
			menu.findItem(R.id.action_detail_fragment)
			    .setChecked(currentOpenDetailActivity);
		}

		boolean useSupportTransition = App.Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
		                                           .getBoolean(App.KEY_USE_SUPPORT_TRANSITION, DEFAULT_USE_SUPPORT_TRANSITION);
		if(useSupportTransition) {
			menu.findItem(R.id.action_use_support_transition)
			    .setChecked(currentOpenDetailActivity);
		}

		boolean useSupportTransitionSimple = App.Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
		                                           .getBoolean(App.KEY_USE_SUPPORT_TRANSITION_SIMPLE, DEFAULT_USE_SUPPORT_TRANSITION_SIMPLE);
		if(useSupportTransitionSimple) {
			menu.findItem(R.id.action_use_support_transition_simple)
			    .setChecked(currentOpenDetailActivity);
		}

		mBinding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				item.setChecked(true);
				switch (item.getItemId()) {
					case R.id.action_detail_activity:
						Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
						        .edit()
						        .putBoolean(App.KEY_OPEN_DETAIL_ACTIVITY, true)
						        .apply();
						Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
						        .edit()
						        .putBoolean(App.KEY_USE_SUPPORT_TRANSITION, false)
						        .apply();
						Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
						        .edit()
						        .putBoolean(App.KEY_USE_SUPPORT_TRANSITION_SIMPLE, false)
						        .apply();
						break;
					case R.id.action_detail_fragment:
						Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
						        .edit()
						        .putBoolean(App.KEY_OPEN_DETAIL_ACTIVITY, false)
						        .apply();
						Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
						        .edit()
						        .putBoolean(App.KEY_USE_SUPPORT_TRANSITION, false)
						        .apply();
						Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
						        .edit()
						        .putBoolean(App.KEY_USE_SUPPORT_TRANSITION_SIMPLE, false)
						        .apply();
						break;
					case R.id.action_use_support_transition:
						Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
						        .edit()
						        .putBoolean(App.KEY_OPEN_DETAIL_ACTIVITY, false)
						        .apply();
						Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
						        .edit()
						        .putBoolean(App.KEY_USE_SUPPORT_TRANSITION, true)
						        .apply();
						Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
						        .edit()
						        .putBoolean(App.KEY_USE_SUPPORT_TRANSITION_SIMPLE, false)
						        .apply();
						break;
					case R.id.action_use_support_transition_simple:
						Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
						        .edit()
						        .putBoolean(App.KEY_OPEN_DETAIL_ACTIVITY, false)
						        .apply();
						Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
						        .edit()
						        .putBoolean(App.KEY_USE_SUPPORT_TRANSITION, false)
						        .apply();
						Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
						        .edit()
						        .putBoolean(App.KEY_USE_SUPPORT_TRANSITION_SIMPLE, true)
						        .apply();
						break;
				}
				return true;
			}
		});

		mBinding.imagesListRv.setItemAnimator(new DefaultItemAnimator());
		loadOrSetData();
	}

	@Override
	public void onDestroyView() {
		if (mBinding.imagesListRv.getAdapter() != null) {
			ImagesResponseAdapter adp = (ImagesResponseAdapter) mBinding.imagesListRv.getAdapter();
			mLastImagesList = adp.getImagesList();
		}
		super.onDestroyView();
	}

	private void loadOrSetData() {
		if (mLastImagesList != null) {
			//Set data if already loaded.
			setData(mLastImagesList);
			return;
		}

		//Load data when no-data.
		mBinding.activityMainSrl.setRefreshing(true);
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

	private synchronized void setData(List<Image> imagesList) {
		if (imagesList == null) {
			return;
		}
		if (mBinding.imagesListRv.getAdapter() == null) {
			mBinding.imagesListRv.setAdapter(new ImagesResponseAdapter(new ArrayList<Image>()));
		}
		ImagesResponseAdapter adp = (ImagesResponseAdapter) mBinding.imagesListRv.getAdapter();
		adp.getImagesList()
		   .addAll(imagesList);
		adp.notifyDataSetChanged();
	}

	private synchronized  void clearData() {
		mLastImagesList = null;
		if (mBinding.imagesListRv.getAdapter() != null) {
			ImagesResponseAdapter adp = (ImagesResponseAdapter) mBinding.imagesListRv.getAdapter();
			adp.getImagesList()
			   .clear();
			adp.notifyDataSetChanged();
		} else {
			mBinding.imagesListRv.setAdapter(new ImagesResponseAdapter(new ArrayList<Image>()));
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
				mBinding.activityMainSrl.setRefreshing(false);
			}

			@Override
			public void onFailure(Call<ImagesResponse> call, Throwable t) {
				EventBus.getDefault()
				        .post(new LoadImagesErrorEvent());
				mBinding.activityMainSrl.setRefreshing(false);
			}
		});
	}


	public static class DividerDecoration extends RecyclerView.ItemDecoration {
		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			super.getItemOffsets(outRect, view, parent, state);
			if (!shouldDrawDividerAbove(view, parent)) {
				outRect.top = Instance.getResources()
				                      .getDimensionPixelOffset(R.dimen.divide_height);
			}
		}

		private boolean shouldDrawDividerAbove(View view, RecyclerView parent) {
			final RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
			return holder.getAdapterPosition() == 0;
		}
	}
}
