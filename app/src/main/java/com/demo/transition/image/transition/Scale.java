package com.demo.transition.image.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionValues;
import android.support.transition.Visibility;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;


public final class Scale extends Visibility {
	public static final int DURATION = 1000;
	private final float mStartXValue;
	private final float mStartYValue;


	public Scale(float startXValue, float startYValue) {
		mStartXValue = startXValue;
		mStartYValue = startYValue;
	}


	@Nullable
	@Override
	public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
		View view = sceneRoot;

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			int startRadius = 0;
			int endRadius = Math.max(sceneRoot.getWidth(), sceneRoot.getHeight());
			int centerX = (int) ViewCompat.getPivotX(sceneRoot);
			int centerY = (int) ViewCompat.getPivotY(sceneRoot);
			Animator reveal = ViewAnimationUtils.createCircularReveal(sceneRoot, centerX, centerY, startRadius, endRadius);
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
}
