package com.demo.transition.image.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionValues;
import android.support.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


public final class Scale extends Visibility {
	private float mStartXValue;
	private float mStartYValue;


	public Scale(float startXValue, float startYValue) {
		mStartXValue = startXValue;
		mStartYValue = startYValue;
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

	@Nullable
	@Override
	public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
		View view = sceneRoot;
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(ObjectAnimator.ofFloat(view, "scaleX", mStartXValue, 1)
		                                       .setDuration(2000),
		                         ObjectAnimator.ofFloat(view, "scaleY", mStartYValue, 1)
		                                       .setDuration(2000));
		return animatorSet;
	}
}
