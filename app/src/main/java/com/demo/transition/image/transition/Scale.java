package com.demo.transition.image.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionValues;
import android.support.transition.Visibility;
import android.view.View;
import android.view.ViewGroup;


public final class Scale extends Visibility {
	private View mTarget;
	private float mStartXValue;
	private float mStartYValue;


	public Scale(View target, float startXValue, float startYValue) {
		mTarget = target;
		mStartXValue = startXValue;
		mStartYValue = startYValue;
	}

	@Override
	public void captureEndValues(@NonNull TransitionValues transitionValues) {

	}

	@Override
	public void captureStartValues(@NonNull TransitionValues transitionValues) {

	}

	@Nullable
	@Override
	public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(ObjectAnimator.ofFloat(mTarget, "scaleX", mStartXValue, 1)
		                                       .setDuration(2000),
		                         ObjectAnimator.ofFloat(mTarget, "scaleY", mStartYValue, 1)
		                                       .setDuration(2000));
		return animatorSet;
	}
}
