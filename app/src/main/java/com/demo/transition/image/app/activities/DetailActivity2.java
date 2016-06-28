package com.demo.transition.image.app.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.demo.transition.image.R;
import com.demo.transition.image.app.fragments.DetailFragment;
import com.demo.transition.image.bus.CloseDetailActivity2Event;
import com.demo.transition.image.bus.CloseDetailFragmentEvent;
import com.demo.transition.image.ds.Image;
import com.demo.transition.image.transition.Thumbnail;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;

import static android.os.Bundle.EMPTY;

public final class DetailActivity2 extends BaseActivity {
	private static final String EXTRAS_IMAGE = DetailActivity.class.getName() + ".EXTRAS.image";
	private static final String EXTRAS_THUMBNAIL = DetailActivity.class.getName() + ".EXTRAS.thumbnail";
	private static final int LAYOUT = R.layout.activity_detail_2;
	private static final int MENU_DETAIL = R.menu.menu_detail;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link CloseDetailActivity2Event}.
	 *
	 * @param e Event {@link CloseDetailActivity2Event}.
	 */
	@Subscribe
	public void onEvent(CloseDetailActivity2Event e) {
		ActivityCompat.finishAfterTransition(this);
	}
	//------------------------------------------------


	static void showInstance(Activity cxt, Image image, Thumbnail thumbnail) {
		Intent intent = new Intent(cxt, DetailActivity2.class);
		intent.putExtra(EXTRAS_IMAGE, image);
		intent.putExtra(EXTRAS_THUMBNAIL, thumbnail);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ActivityCompat.startActivity(cxt, intent, EMPTY);
	}


	static void showInstance(Activity cxt, Image image) {
		Intent intent = new Intent(cxt, DetailActivity2.class);
		intent.putExtra(EXTRAS_IMAGE, image);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ActivityCompat.startActivity(cxt, intent, EMPTY);
	}


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);

		Intent intent = getIntent();
		Image image = (Image) intent.getSerializableExtra(EXTRAS_IMAGE);
		Thumbnail thumbnail = (Thumbnail) intent.getSerializableExtra(EXTRAS_THUMBNAIL);

		getSupportFragmentManager().beginTransaction()
		                           .replace(R.id.activity_detail_2_fl, DetailFragment.newInstance(image, thumbnail))
		                           .commit();
	}

	@Override
	public void onBackPressed() {
		EventBus.getDefault().post(new CloseDetailFragmentEvent());
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
