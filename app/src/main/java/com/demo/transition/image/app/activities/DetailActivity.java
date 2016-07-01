package com.demo.transition.image.app.activities;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.animation.AnimatorUpdateListenerCompat;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;

import com.demo.transition.image.R;
import com.demo.transition.image.app.App;
import com.demo.transition.image.databinding.LayoutDetailBinding;
import com.demo.transition.image.ds.Image;
import com.demo.transition.image.transition.BakedBezierInterpolator;
import com.demo.transition.image.transition.Thumbnail;
import com.demo.transition.image.transition.TransitCompat;
import com.demo.transition.image.utils.Utils;

import java.io.Serializable;

import static android.os.Bundle.EMPTY;

public final class DetailActivity extends BaseActivity {
	private static final String EXTRAS_IMAGE = DetailActivity.class.getName() + ".EXTRAS.image";
	private static final String EXTRAS_THUMBNAIL = DetailActivity.class.getName() + ".EXTRAS.thumbnail";
	private static final int LAYOUT = R.layout.layout_detail;
	private static final int MENU_DETAIL = R.menu.menu_detail;
	private LayoutDetailBinding mBinding;
	private TransitCompat mTransition;

	public static void showInstance(Activity cxt, Image image, Thumbnail thumbnail) {
		Intent intent = new Intent(cxt, DetailActivity.class);
		intent.putExtra(EXTRAS_IMAGE, image);
		intent.putExtra(EXTRAS_THUMBNAIL, thumbnail);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ActivityCompat.startActivity(cxt, intent, EMPTY);
	}


	public static void showInstance(Activity cxt, Image image, Thumbnail thumbnail, ActivityOptionsCompat options) {
		Intent intent = new Intent(cxt, DetailActivity.class);
		intent.putExtra(EXTRAS_IMAGE, image);
		intent.putExtra(EXTRAS_THUMBNAIL, thumbnail);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ActivityCompat.startActivity(cxt, intent, options.toBundle());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = DataBindingUtil.setContentView(this, LAYOUT);
		setSupportActionBar(mBinding.toolbar);

		mBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
		mBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ActivityCompat.finishAfterTransition(DetailActivity.this);
			}
		});

		Serializable imageMeta = getIntent().getSerializableExtra(EXTRAS_IMAGE);
		Image image = (Image) imageMeta;
		setTitle(image.getTitle());

		mBinding.setImage(image);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			transitCompat();
		}
		Snackbar.make(mBinding.detailRootCl, R.string.action_detail_activity, Snackbar.LENGTH_SHORT)
		        .show();
	}


	private void transitCompat() {
		mBinding.detailAppBar.getLayoutParams().height = Utils.getScreenSize(App.Instance).Height;
		Intent intent = getIntent();
		Object thumbnail = intent.getSerializableExtra(EXTRAS_THUMBNAIL);
		if (thumbnail != null) {
			// Only run the animation if we're coming from the parent activity, not if
			// we're recreated automatically by the window manager (e.g., device rotation)
			ViewTreeObserver observer = mBinding.imageIv.getViewTreeObserver();
			observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					Intent intent = getIntent();
					Object object = intent.getSerializableExtra(EXTRAS_THUMBNAIL);
					mBinding.imageIv.getViewTreeObserver()
					                .removeOnPreDrawListener(this);

					ValueAnimatorCompat a1 = AnimatorCompatHelper.emptyValueAnimator();
					a1.setDuration(TransitCompat.ANIM_DURATION);
					final Interpolator interpolator1 = new BakedBezierInterpolator();
					a1.addUpdateListener(new AnimatorUpdateListenerCompat() {
						private float oldHeight = Utils.getScreenSize(App.Instance).Height;
						private float endHeight = getResources().getDimension(R.dimen.detail_backdrop_height);
						@Override
						public void onAnimationUpdate(ValueAnimatorCompat animation) {
							float fraction = interpolator1.getInterpolation(animation.getAnimatedFraction());
							float currentHeight = oldHeight + (fraction * (endHeight - oldHeight));
							mBinding.detailAppBar.getLayoutParams().height = (int) currentHeight;
							mBinding.detailAppBar.requestLayout();
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
					                                         .setPlayTogetherAfterEnterValueAnimators(a1)
					                                         .setPlayTogetherBeforeExitValueAnimators(a2)
					                                         .build();
					mTransition.enter(new ViewPropertyAnimatorListenerAdapter());
					return true;
				}
			});
		}
	}


	@Override
	public void onBackPressed() {
		if (mTransition != null) {
			mTransition.exit(new ViewPropertyAnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(View v) {
					super.onAnimationEnd(v);
					ActivityCompat.finishAfterTransition(DetailActivity.this);
				}
			});
		} else {
			ActivityCompat.finishAfterTransition(DetailActivity.this);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(MENU_DETAIL, menu);
		MenuItem item = menu.findItem(R.id.action_share);
		ShareActionProvider myShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
		Intent myShareIntent = new Intent(Intent.ACTION_SEND);
		myShareIntent.setType("text/plain");
		Serializable imageMeta = getIntent().getSerializableExtra(EXTRAS_IMAGE);
		Image image = (Image) imageMeta;
		myShareIntent.putExtra(Intent.EXTRA_TEXT,
		                       String.format("%s",
		                                     image.getImageUrl()
		                                          .getHd()));
		myShareActionProvider.setShareIntent(myShareIntent);
		return true;
	}
}
