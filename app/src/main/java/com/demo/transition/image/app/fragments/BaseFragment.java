package com.demo.transition.image.app.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.demo.transition.image.R;
import com.demo.transition.image.app.App;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class BaseFragment extends Fragment {
	private int mActionBarHeight;


	/**
	 * Handler for {@link Object}.
	 *
	 * @param e Event {@link Object}.
	 */
	@Subscribe
	public void onEvent(Object e) {

	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActionBarHeight = calcActionBarHeight(App.Instance);
	}

	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault()
		        .register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		EventBus.getDefault()
		        .unregister(this);
	}

	protected int getActionBarHeight() {
		return mActionBarHeight;
	}

	private static int calcActionBarHeight(Context cxt) {
		int[] abSzAttr;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			abSzAttr = new int[] { android.R.attr.actionBarSize };
		} else {
			abSzAttr = new int[] { R.attr.actionBarSize };
		}
		@SuppressLint("Recycle") TypedArray a = cxt.obtainStyledAttributes(abSzAttr);
		return a.getDimensionPixelSize(0, -1);
	}


}
