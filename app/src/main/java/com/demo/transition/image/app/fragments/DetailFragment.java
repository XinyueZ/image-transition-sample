package com.demo.transition.image.app.fragments;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.animation.AnimatorUpdateListenerCompat;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;

import com.demo.transition.image.R;
import com.demo.transition.image.app.App;
import com.demo.transition.image.bus.CloseDetailFragmentEvent;
import com.demo.transition.image.bus.PopUpDetailFragmentEvent;
import com.demo.transition.image.databinding.LayoutDetailBinding;
import com.demo.transition.image.ds.Image;
import com.demo.transition.image.transition.BakedBezierInterpolator;
import com.demo.transition.image.transition.Thumbnail;
import com.demo.transition.image.transition.TransitCompat;
import com.demo.transition.image.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;

public final class DetailFragment extends BaseFragment {
	private static final String EXTRAS_IMAGE = DetailFragment.class.getName() + ".EXTRAS.image";
	private static final String EXTRAS_THUMBNAIL = DetailFragment.class.getName() + ".EXTRAS.thumbnail";
	private static final String EXTRAS_TRANSITION_NAME = DetailFragment.class.getName() + ".EXTRAS.transition_name";
	private static final int LAYOUT = R.layout.layout_detail;
	private static final int MENU_DETAIL = R.menu.menu_detail;
	private LayoutDetailBinding mBinding;
	private TransitCompat mTransition;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link CloseDetailFragmentEvent}.
	 *
	 * @param e Event {@link CloseDetailFragmentEvent}.
	 */
	@Subscribe
	public void onEvent(@SuppressWarnings("UnusedParameters") CloseDetailFragmentEvent e) {
		closeThisFragment();
	}


	//------------------------------------------------

	public static Fragment newInstance(Image image, Thumbnail thumbnail) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRAS_IMAGE, image);
		args.putSerializable(EXTRAS_THUMBNAIL, thumbnail);
		return Fragment.instantiate(App.Instance, DetailFragment.class.getName(), args);
	}

	public static Fragment newInstance(Image image, Thumbnail thumbnail, String transitionName) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRAS_IMAGE, image);
		args.putSerializable(EXTRAS_THUMBNAIL, thumbnail);
		args.putString(EXTRAS_TRANSITION_NAME, transitionName);
		return Fragment.instantiate(App.Instance, DetailFragment.class.getName(), args);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
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

		Serializable imageMeta = getArguments().getSerializable(EXTRAS_IMAGE);
		Image image = (Image) imageMeta;
		mBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
		mBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				EventBus.getDefault()
				        .post(new PopUpDetailFragmentEvent());
			}
		});
		mBinding.toolbar.inflateMenu(MENU_DETAIL);
		MenuItem menuShare = mBinding.toolbar.getMenu()
		                                     .findItem(R.id.action_share);
		ShareActionProvider myShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuShare);
		Intent myShareIntent = new Intent(Intent.ACTION_SEND);
		myShareIntent.setType("text/plain");
		myShareIntent.putExtra(Intent.EXTRA_TEXT,
		                       String.format("%s",
		                                     image.getImageUrl()
		                                          .getHd()));
		myShareActionProvider.setShareIntent(myShareIntent);

		mBinding.toolbar.setTitle(image.getTitle());
		mBinding.setImage(image);


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			ViewCompat.setTransitionName(mBinding.imageIv, getArguments().getString(EXTRAS_TRANSITION_NAME));
		}
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			transitCompat();
		}
		Snackbar.make(mBinding.detailRootCl, R.string.action_detail_fragment, Snackbar.LENGTH_SHORT)
		        .show();
	}


	private void transitCompat() {
		mBinding.detailAppBar.getLayoutParams().height = Utils.getScreenSize(App.Instance).Height;
		mBinding.imageInformationNsv.getBackground().setAlpha(0);
		Object thumbnail = getArguments().getSerializable(EXTRAS_THUMBNAIL);
		if (thumbnail != null) {
			// Only run the animation if we're coming from the parent activity, not if
			// we're recreated automatically by the window manager (e.g., device rotation)
			ViewTreeObserver observer = mBinding.imageIv.getViewTreeObserver();
			observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					Object object = getArguments().getSerializable(EXTRAS_THUMBNAIL);
					mBinding.imageIv.getViewTreeObserver()
					                .removeOnPreDrawListener(this);

					ValueAnimatorCompat a1 = AnimatorCompatHelper.emptyValueAnimator();
					a1.setDuration(TransitCompat.ANIM_DURATION * 2);
					final Interpolator interpolator1 = new BakedBezierInterpolator();
					a1.addUpdateListener(new AnimatorUpdateListenerCompat() {
						private float oldHeight = Utils.getScreenSize(App.Instance).Height;
						private float endHeight = getResources().getDimension(R.dimen.detail_backdrop_height);

						private float oldBgAlpha = 0;
						private float endBgAlpha = 255;

						@Override
						public void onAnimationUpdate(ValueAnimatorCompat animation) {
							float fraction = interpolator1.getInterpolation(animation.getAnimatedFraction());

							//Set height
							float currentHeight = oldHeight + (fraction * (endHeight - oldHeight));
							mBinding.detailAppBar.getLayoutParams().height = (int) currentHeight;
							mBinding.detailAppBar.requestLayout();

							//Set background alpha
							mBinding.imageInformationNsv.getBackground().setAlpha((int) (oldBgAlpha + (fraction * (endBgAlpha - oldBgAlpha))));
							mBinding.imageInformationNsv.invalidate();
						}
					});

					ValueAnimatorCompat a2 = AnimatorCompatHelper.emptyValueAnimator();
					a2.setDuration(TransitCompat.ANIM_DURATION);
					final Interpolator interpolator2 = new BakedBezierInterpolator();
					a2.addUpdateListener(new AnimatorUpdateListenerCompat() {
						private float oldHeight =getResources().getDimension(R.dimen.detail_backdrop_height);
						private float endHeight =  Utils.getScreenSize(App.Instance).Height ;
						@Override
						public void onAnimationUpdate(ValueAnimatorCompat animation) {
							float fraction = interpolator2.getInterpolation(animation.getAnimatedFraction());
							float currentHeight = oldHeight + (fraction * (endHeight - oldHeight));
							mBinding.detailAppBar.getLayoutParams().height = (int) currentHeight;
							mBinding.detailAppBar.requestLayout();
						}
					});

					mTransition = new TransitCompat.Builder().setThumbnail((Thumbnail) object)
					                                         .setTarget(mBinding.imageIv)
					                                         .setPlayTogetherAfterEnterTransition(a1)
					                                         .setPlayTogetherBeforeExitTransition(a2)
					                                         .build();

					mTransition.enter(new ViewPropertyAnimatorListenerAdapter());
					return true;
				}
			});
		}
	}


	private void closeThisFragment() {
		if (mTransition != null) {
			mTransition.exit(new ViewPropertyAnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(View v) {
					super.onAnimationEnd(v);
					EventBus.getDefault()
					        .post(new PopUpDetailFragmentEvent());
				}
			});
		} else {
			EventBus.getDefault()
			        .post(new PopUpDetailFragmentEvent());
		}
	}
}
