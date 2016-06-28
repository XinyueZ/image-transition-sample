package com.demo.transition.image.app.fragments;


import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.demo.transition.image.R;
import com.demo.transition.image.app.App;
import com.demo.transition.image.bus.CloseDetailActivity2Event;
import com.demo.transition.image.bus.CloseDetailFragmentEvent;
import com.demo.transition.image.databinding.FragmentDetailBinding;
import com.demo.transition.image.ds.Image;
import com.demo.transition.image.transition.Thumbnail;
import com.demo.transition.image.transition.TransitCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;

public final class DetailFragment extends Fragment {
	private static final String EXTRAS_IMAGE = DetailFragment.class.getName() + ".EXTRAS.image";
	private static final String EXTRAS_THUMBNAIL = DetailFragment.class.getName() + ".EXTRAS.thumbnail";
	private static final int LAYOUT = R.layout.fragment_detail;
	private FragmentDetailBinding mBinding;
	private TransitCompat mTransition;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link CloseDetailFragmentEvent}.
	 * @param e Event {@link CloseDetailFragmentEvent}.
	 */
	@Subscribe
	public void onEvent(@SuppressWarnings("UnusedParameters") CloseDetailFragmentEvent e) {
		if (mTransition != null) {
			mTransition.exit(new ViewPropertyAnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(View view) {
					super.onAnimationStart(view);
					mBinding.imageInformationNsv.setVisibility(View.GONE);
					mBinding.detailAppBar.setBackgroundResource(android.R.color.transparent);
				}

				@Override
				public void onAnimationEnd(View v) {
					super.onAnimationEnd(v);
					EventBus.getDefault().post(new CloseDetailActivity2Event());
				}
			});
		} else {
			EventBus.getDefault().post(new CloseDetailActivity2Event());
		}
	}

	//------------------------------------------------

	public static Fragment newInstance(Image image, Thumbnail thumbnail) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRAS_IMAGE, image);
		args.putSerializable(EXTRAS_THUMBNAIL, thumbnail);
		return Fragment.instantiate(App.Instance, DetailFragment.class.getName(), args);
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
		mBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
		mBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				EventBus.getDefault().post(new CloseDetailActivity2Event());
			}
		});

		Serializable imageMeta = getArguments().getSerializable(EXTRAS_IMAGE);
		Image image = (Image) imageMeta;
		mBinding.toolbar.setTitle(image.getTitle());
		mBinding.setImage(image);
		transitCompat();
	}

	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		EventBus.getDefault().unregister(this);
	}


	private void transitCompat() {
		Object thumbnail = getArguments().getSerializable(EXTRAS_THUMBNAIL);
		if (thumbnail != null) {
			// Only run the animation if we're coming from the parent activity, not if
			// we're recreated automatically by the window manager (e.g., device rotation)
			ViewTreeObserver observer = mBinding.imageIv.getViewTreeObserver();
			observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					Activity activity = getActivity();
					if (activity != null) {
						Object object = getArguments().getSerializable(EXTRAS_THUMBNAIL);
						mBinding.imageIv.getViewTreeObserver()
						                .removeOnPreDrawListener(this);
						mTransition = new TransitCompat.Builder(App.Instance).setThumbnail((Thumbnail) object)
						                                                     .setTarget(mBinding.imageIv)
						                                                     .setTransistor(mBinding.tempIv)
						                                                     .build(activity);
						mTransition.enter(new ViewPropertyAnimatorListenerAdapter() {
							@Override
							public void onAnimationEnd(View view) {
								super.onAnimationEnd(view);
								mBinding.imageInformationNsv.setVisibility(View.VISIBLE);
								mBinding.detailAppBar.setBackgroundResource(R.color.colorPrimary);
							}
						});
					}

					return true;
				}
			});
		}
	}
}
