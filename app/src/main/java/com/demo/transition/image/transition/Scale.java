package com.demo.transition.image.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionValues;
import android.support.transition.Visibility;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


public final class Scale extends Visibility {
	public static final int DURATION = 500;
	private float mStartXValue = 0;
	private float mStartYValue = 0;


	public Scale() {
		super();
	}

	public Scale(float startXValue, float startYValue) {
		super();
		mStartXValue = startXValue;
		mStartYValue = startYValue;
	}


	@Nullable
	@Override
	public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
		Log.d(TAG, "Scale: ");
		View view = sceneRoot;

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			int startRadius = 0;
			int endRadius = Math.max(sceneRoot.getWidth(), sceneRoot.getHeight());
			int startX = (int) ViewCompat.getPivotX(sceneRoot);
			int startY = (int) ViewCompat.getPivotY(sceneRoot);
			Animator reveal = ViewAnimationUtils.createCircularReveal(sceneRoot, startX, startY, startRadius, endRadius);
			reveal.setInterpolator(new BakedBezierInterpolator());
			reveal.setDuration(DURATION);
			return reveal;
		}

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(ObjectAnimator.ofFloat(view, "scaleX", mStartXValue, 1)
		                                       .setDuration(DURATION),
		                         ObjectAnimator.ofFloat(view, "scaleY", mStartYValue, 1)
		                                       .setDuration(DURATION));
		return animatorSet;
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
