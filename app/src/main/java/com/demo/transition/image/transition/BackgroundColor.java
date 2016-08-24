package com.demo.transition.image.transition;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionValues;
import android.support.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public final class BackgroundColor extends Visibility {
	public static final int DURATION = 500;
	private final int mStartColor;
	private final int mEndColor;

	public BackgroundColor(int startColor, int endColor) {
		mStartColor = startColor;
		mEndColor = endColor;
	}

	@Nullable
	@Override
	public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
		View view = sceneRoot;
		Log.d(TAG, "BackgroundColor: ");
		return ObjectAnimator.ofInt(view, "backgroundColor", mStartColor, mEndColor)
		                     .setDuration(DURATION);
	}

	@Override
	public void captureEndValues(@NonNull TransitionValues transitionValues) {
	}

	@Override
	public void captureStartValues(@NonNull TransitionValues transitionValues) {
	}

	@Override
	public Animator onDisappear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
		Log.d(TAG, "onDisappear: ");
		return super.onDisappear(sceneRoot, startValues, startVisibility, endValues, endVisibility);
	}

	@Override
	public Animator onAppear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
		Log.d(TAG, "onAppear: ");
		return super.onAppear(sceneRoot, startValues, startVisibility, endValues, endVisibility);
	}
}
