package com.demo.transition.image.app.fragments;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.transition.Fade;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.demo.transition.image.R;
import com.demo.transition.image.app.App;
import com.demo.transition.image.bus.CloseDetailFragmentEvent;
import com.demo.transition.image.bus.PopUpDetailFragmentEvent;
import com.demo.transition.image.databinding.LayoutDetailAfterTransBinding;
import com.demo.transition.image.databinding.LayoutDetailBeforeTransBinding;
import com.demo.transition.image.ds.Image;
import com.demo.transition.image.transition.BakedBezierInterpolator;
import com.demo.transition.image.transition.Scale;
import com.demo.transition.image.transition.Thumbnail;
import com.demo.transition.image.transition.TransitCompat;
import com.demo.transition.image.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;

public final class DetailWithSupportTransitionSimpleFragment extends BaseFragment {
	private static final String EXTRAS_IMAGE = DetailWithSupportTransitionSimpleFragment.class.getName() + ".EXTRAS.image";
	private static final String EXTRAS_THUMBNAIL = DetailFragment.class.getName() + ".EXTRAS.thumbnail";
	private static final int LAYOUT_BEFORE_TRANSITION = R.layout.layout_detail_before_transition;
	private static final int LAYOUT_AFTER_TRANSITION = R.layout.layout_detail_after_transition;
	private static final int LAYOUT = R.layout.fragment_detail_with_support_transition;
	private static final int MENU_DETAIL = R.menu.menu_detail;
	private LayoutDetailBeforeTransBinding mBeforeTransBinding;
	private LayoutDetailAfterTransBinding mAfterTransBinding;

	private TransitionManager mTransitionManager;
	private Scene mSceneBefore;
	private Scene mSceneAfter;
	private TransitionSet mTransitionSet;

	private Utils.ScreenSize mScreenSize;


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
		return Fragment.instantiate(App.Instance, DetailWithSupportTransitionSimpleFragment.class.getName(), args);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mScreenSize = Utils.getScreenSize(App.Instance);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		ViewGroup rootV = (ViewGroup) inflater.inflate(LAYOUT, container, false);
		View beforeV = inflater.inflate(LAYOUT_BEFORE_TRANSITION, container, false);
		View afterV = inflater.inflate(LAYOUT_AFTER_TRANSITION, container, false);
		mSceneBefore = new Scene(rootV, beforeV);
		mSceneAfter = new Scene(rootV, afterV);

		mBeforeTransBinding = DataBindingUtil.bind(beforeV);
		mAfterTransBinding = DataBindingUtil.bind(afterV);

		//Bravo begins!
		mTransitionManager = new TransitionManager();
		mTransitionSet = new TransitionSet();
		mTransitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
		mTransitionSet.setInterpolator(new BakedBezierInterpolator());
		mTransitionSet.addTransition(new Scale(0.5f, 0.5f))
		              .addTransition(new Fade(Fade.IN));
		mTransitionSet.setDuration(TransitCompat.ANIM_DURATION);
		mTransitionManager.setTransition(mSceneBefore, mSceneAfter, mTransitionSet);

		return rootV;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mTransitionManager.transitionTo(mSceneBefore);//Bravo!

		Serializable imageMeta = getArguments().getSerializable(EXTRAS_IMAGE);
		Image image = (Image) imageMeta;
		Thumbnail thumbnail = (Thumbnail) getArguments().getSerializable(EXTRAS_THUMBNAIL);
		mBeforeTransBinding.imageIv.setX(thumbnail.getLeft());
		mBeforeTransBinding.imageIv.setY(thumbnail.getTop());
		mBeforeTransBinding.imageIv.getLayoutParams().width = thumbnail.getWidth();
		mBeforeTransBinding.imageIv.getLayoutParams().height = thumbnail.getHeight();
		ViewCompat.setPivotX(view, thumbnail.getLeft());
		ViewCompat.setPivotY(view, thumbnail.getTop());
		mBeforeTransBinding.setImage(image);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mTransitionManager.transitionTo(mSceneAfter);//Bravo!

				Snackbar.make(mAfterTransBinding.detailRootCl, R.string.action_use_support_transition_simple, Snackbar.LENGTH_SHORT)
				        .show();
			}
		}, 100);


		mAfterTransBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
		mAfterTransBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				closeThisFragment();
			}
		});
		mAfterTransBinding.toolbar.inflateMenu(MENU_DETAIL);
		MenuItem menuShare = mAfterTransBinding.toolbar.getMenu()
		                                               .findItem(R.id.action_share);
		ShareActionProvider myShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuShare);
		Intent myShareIntent = new Intent(Intent.ACTION_SEND);
		myShareIntent.setType("text/plain");
		myShareIntent.putExtra(Intent.EXTRA_TEXT,
		                       String.format("%s",
		                                     image.getImageUrl()
		                                          .getHd()));
		myShareActionProvider.setShareIntent(myShareIntent);
		mAfterTransBinding.setImage(image);
	}


	private void closeThisFragment() {
		EventBus.getDefault()
		        .post(new PopUpDetailFragmentEvent());
	}
}
