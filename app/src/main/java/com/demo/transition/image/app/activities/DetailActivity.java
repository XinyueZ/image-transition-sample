package com.demo.transition.image.app.activities;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.demo.transition.image.R;
import com.demo.transition.image.databinding.ActivityDetailBinding;
import com.demo.transition.image.ds.Image;

import java.io.Serializable;

import static android.os.Bundle.EMPTY;

public final class DetailActivity extends  BaseActivity {
	private static final String EXTRAS_IMAGE = DetailActivity.class.getName() + ".EXTRAS.image";
	private static final int LAYOUT = R.layout.activity_detail;
	private static final int MENU_DETAIL = R.menu.menu_detail;
	private ActivityDetailBinding mBinding;

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
		mBinding.setImage(image);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(MENU_DETAIL, menu);
		MenuItem item = menu.findItem(R.id.menu_item_share);
		ShareActionProvider myShareActionProvider =
				(ShareActionProvider) MenuItemCompat.getActionProvider(item);
		Intent myShareIntent = new Intent(Intent.ACTION_SEND);
		myShareIntent.setType("text/plain");
		Serializable imageMeta = getIntent().getSerializableExtra(EXTRAS_IMAGE);
		Image image = (Image) imageMeta;
		myShareIntent.putExtra(Intent.EXTRA_TEXT, String.format("%s",image.getImageUrl().getHd()));
		myShareActionProvider.setShareIntent(myShareIntent);

		return true;
	}

}
