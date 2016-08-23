package com.demo.transition.image.app.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.transition.Fade;
import android.widget.ImageView;

import com.demo.transition.image.R;
import com.demo.transition.image.app.App;
import com.demo.transition.image.app.fragments.DetailFragment;
import com.demo.transition.image.app.fragments.DetailWithSupportTransitionFragment;
import com.demo.transition.image.app.fragments.DetailWithSupportTransitionSimpleFragment;
import com.demo.transition.image.app.fragments.MainFragment;
import com.demo.transition.image.bus.ClickImageEvent;
import com.demo.transition.image.bus.CloseDetailFragmentEvent;
import com.demo.transition.image.bus.PopUpDetailFragmentEvent;
import com.demo.transition.image.transition.v21.PlatformTransition;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;

import static com.demo.transition.image.app.App.DEFAULT_USE_SUPPORT_TRANSITION;
import static com.demo.transition.image.app.App.DEFAULT_USE_SUPPORT_TRANSITION_SIMPLE;
import static com.demo.transition.image.app.App.KEY_OPEN_DETAIL_ACTIVITY;
import static com.demo.transition.image.app.App.PREFS;

public final class MainActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener {

	private static final int LAYOUT = R.layout.activity_main;
	private static final int MAIN_CONTAINER = R.id.main_fl;
	private Fragment mMainFragment;
	private WeakReference<ImageView> mThumbnailIvRef;

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
		boolean useSupportTransition = App.Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
		                                           .getBoolean(App.KEY_USE_SUPPORT_TRANSITION, DEFAULT_USE_SUPPORT_TRANSITION);
		boolean useSupportTransitionSimple = App.Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
		                                           .getBoolean(App.KEY_USE_SUPPORT_TRANSITION_SIMPLE, DEFAULT_USE_SUPPORT_TRANSITION_SIMPLE);
		if(useSupportTransition || useSupportTransitionSimple) {
			getSupportFragmentManager().addOnBackStackChangedListener(this);
			mThumbnailIvRef = e.getSharedImageViewWeakRef();
			if(mThumbnailIvRef != null && mThumbnailIvRef.get() != null) {
				ViewCompat.animate(mThumbnailIvRef.get()).alpha(0).start();
			}
			Fragment targetFrg = useSupportTransition ? DetailWithSupportTransitionFragment.newInstance(e.getImage(), e.getThumbnail()) :
			                     DetailWithSupportTransitionSimpleFragment.newInstance(e.getImage(), e.getThumbnail()) ;
			getSupportFragmentManager().beginTransaction()
			                           .add(MAIN_CONTAINER, targetFrg)
			                           .addToBackStack(null)
			                           .commit();
			return;
		}

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
		boolean useSupportTransition = App.Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
		                                           .getBoolean(App.KEY_USE_SUPPORT_TRANSITION, DEFAULT_USE_SUPPORT_TRANSITION);
		boolean useSupportTransitionSimple = App.Instance.getSharedPreferences(App.PREFS, Context.MODE_PRIVATE)
		                                                 .getBoolean(App.KEY_USE_SUPPORT_TRANSITION_SIMPLE, DEFAULT_USE_SUPPORT_TRANSITION_SIMPLE);
		if(useSupportTransition || useSupportTransitionSimple) {
			if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
				super.onBackPressed();
			} else {
				EventBus.getDefault()
				        .post(new CloseDetailFragmentEvent());
			}
			return;
		}

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

	@Override
	public void onBackStackChanged() {
		if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
			if(mThumbnailIvRef != null && mThumbnailIvRef.get() != null) {
				ViewCompat.animate(mThumbnailIvRef.get()).alpha(1).start();
			}
		}
	}

	@Override
	protected void onStop() {
		mThumbnailIvRef = null;
		super.onStop();
	}
}
