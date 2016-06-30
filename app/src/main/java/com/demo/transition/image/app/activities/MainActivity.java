package com.demo.transition.image.app.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.transition.Fade;

import com.demo.transition.image.R;
import com.demo.transition.image.app.App;
import com.demo.transition.image.app.fragments.DetailFragment;
import com.demo.transition.image.app.fragments.MainFragment;
import com.demo.transition.image.bus.ClickImageEvent;
import com.demo.transition.image.bus.CloseDetailFragmentEvent;
import com.demo.transition.image.bus.PopUpDetailFragmentEvent;
import com.demo.transition.image.transition.v21.PlatformTransition;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.demo.transition.image.app.App.KEY_OPEN_DETAIL_ACTIVITY;
import static com.demo.transition.image.app.App.PREFS;

public final class MainActivity extends BaseActivity {

	private static final int LAYOUT = R.layout.activity_main;
	private static final int MAIN_CONTAINER = R.id.main_fl;
	private Fragment mMainFragment;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link PopUpDetailFragmentEvent}.
	 *
	 * @param e Event {@link PopUpDetailFragmentEvent}.
	 */
	@Subscribe
	public void onEvent(@SuppressWarnings("UnusedParameters") PopUpDetailFragmentEvent e) {
		super.onBackPressed();
	}


	/**
	 * Handler for {@link  ClickImageEvent}.
	 *
	 * @param e Event {@link ClickImageEvent}.
	 */
	@Subscribe
	public void onEvent(ClickImageEvent e) {
		if (e.getThumbnail() != null) {
			boolean currentOpenDetailActivity = App.Instance.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
			                                                .getBoolean(KEY_OPEN_DETAIL_ACTIVITY, App.DEFAULT_OPEN_DETAIL_ACTIVITY);
			e.getThumbnail()
			 .setSource(e.getImage()
			             .getImageUrl()
			             .getNormal());


			if (currentOpenDetailActivity) {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
					DetailActivity.showInstance(this, e.getImage(), e.getThumbnail());
				} else {
					ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, e.getSharedImageView(), App.Instance.getString(R.string.default_transition_name_detail));
					DetailActivity.showInstance(this, e.getImage(), e.getThumbnail(), options);
				}
			} else {

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
					Fragment targetFrg = DetailFragment.newInstance(e.getImage(), e.getThumbnail());
					getSupportFragmentManager().beginTransaction()
					                           .add(MAIN_CONTAINER, targetFrg)
					                           .addToBackStack(null)
					                           .commit();
				} else {
					String transitionName = ViewCompat.getTransitionName(e.getSharedImageView());
					Fragment targetFrg = DetailFragment.newInstance(e.getImage(), e.getThumbnail(), transitionName);

					targetFrg.setSharedElementEnterTransition(new PlatformTransition());
					targetFrg.setEnterTransition(new Fade());
					mMainFragment.setExitTransition(new Fade());
					targetFrg.setSharedElementReturnTransition(new PlatformTransition());

					getSupportFragmentManager().beginTransaction()
					                           .addSharedElement(e.getSharedImageView(), transitionName)
					                           .replace(MAIN_CONTAINER, targetFrg)
					                           .addToBackStack(null)
					                           .commit();
				}
			}
		}
	}
	//------------------------------------------------


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DataBindingUtil.setContentView(this, LAYOUT);
		getSupportFragmentManager().beginTransaction()
		                           .add(MAIN_CONTAINER, mMainFragment = MainFragment.newInstance())
		                           .commit();
	}


	@Override
	public void onBackPressed() {
		boolean currentOpenDetailActivity = App.Instance.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
		                                                .getBoolean(KEY_OPEN_DETAIL_ACTIVITY, App.DEFAULT_OPEN_DETAIL_ACTIVITY);
		if (!currentOpenDetailActivity) {
			if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
				super.onBackPressed();
			} else {
				EventBus.getDefault()
				        .post(new CloseDetailFragmentEvent());
			}
		} else {
			super.onBackPressed();
		}
	}
}
