package com.demo.transition.image.app.activities;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.demo.transition.image.R;
import com.demo.transition.image.databinding.ActivityDetailBinding;
import com.demo.transition.image.ds.Image;
import com.demo.transition.image.transition.Thumbnail;
import com.demo.transition.image.transition.TransitCompat;
import com.demo.transition.image.utils.ImageViewBindingHelper;

import java.io.Serializable;

import static android.os.Bundle.EMPTY;

public final class DetailActivity extends BaseActivity {
	private static final String EXTRAS_IMAGE = DetailActivity.class.getName() + ".EXTRAS.image";
	private static final String EXTRAS_THUMBNAIL = DetailActivity.class.getName() + ".EXTRAS.thumbnail";
	private static final int LAYOUT = R.layout.activity_detail;
	private static final int MENU_DETAIL = R.menu.menu_detail;
	private ActivityDetailBinding mBinding;
	private TransitCompat mTransition;

	static void showInstance(Activity cxt, Image image, Thumbnail thumbnail) {
		Intent intent = new Intent(cxt, DetailActivity.class);
		intent.putExtra(EXTRAS_IMAGE, image);
		intent.putExtra(EXTRAS_THUMBNAIL, thumbnail);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ActivityCompat.startActivity(cxt, intent, EMPTY);
	}


	static void showInstance(Activity cxt, Image image) {
		Intent intent = new Intent(cxt, DetailActivity.class);
		intent.putExtra(EXTRAS_IMAGE, image);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ActivityCompat.startActivity(cxt, intent, EMPTY);
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

		//Show image detail with thumbnail first.
		//After loading real HD image replace the thumbnail placed ImageView.
		mBinding.setImageLoadedHandler(new RequestListener<String, GlideDrawable>() {
			@Override
			public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
				Serializable imageMeta = getIntent().getSerializableExtra(EXTRAS_IMAGE);
				Image image = (Image) imageMeta;
				ImageViewBindingHelper.setImageLoader(mBinding.imageIv,
				                                      image.getImageUrl()
				                                           .getHd(),
				                                      false);
				return false;
			}

			@Override
			public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
				return false;
			}
		});
		mBinding.setImage(image);
		transitCompat();
	}


	private void transitCompat() {
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
					mTransition = new TransitCompat.Builder().setThumbnail((Thumbnail) object)
					                                         .setTarget(mBinding.imageIv)
					                                         .setTemp(mBinding.tempIv)
					                                         .build(DetailActivity.this);
					mTransition.enter(new ViewPropertyAnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(View view) {
							super.onAnimationEnd(view);
							afterTransitCompat();
						}
					});
					return true;
				}
			});
		}
	}

	private void afterTransitCompat() {
		mBinding.imageInformationNsv.setVisibility(View.VISIBLE);
		mBinding.detailAppBar.setBackgroundResource(R.color.colorPrimary);
	}

	private void beforeTransitCompat() {
		mBinding.imageInformationNsv.setVisibility(View.GONE);
		mBinding.detailAppBar.setBackgroundResource(android.R.color.transparent);
	}

	@Override
	public void onBackPressed() {
		if (mTransition != null) {
			mTransition.exit(new ViewPropertyAnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(View view) {
					beforeTransitCompat();
					super.onAnimationStart(view);
				}

				@Override
				public void onAnimationEnd(View v) {
					super.onAnimationEnd(v);
					ActivityCompat.finishAfterTransition(DetailActivity.this);
				}
			});
		} else {
			super.onBackPressed();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(MENU_DETAIL, menu);
		MenuItem item = menu.findItem(R.id.menu_item_share);
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
