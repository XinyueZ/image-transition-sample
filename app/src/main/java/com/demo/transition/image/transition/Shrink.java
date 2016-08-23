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

public final class Shrink extends Visibility {
	public static final int DURATION = 1000;


	@Nullable
	@Override
	public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
		View view = sceneRoot;

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			int startRadius = Math.max(sceneRoot.getWidth(), sceneRoot.getHeight());
			int endRadius = 0;
			int centerX = (int) ViewCompat.getPivotX(sceneRoot);
			int centerY = (int) ViewCompat.getPivotY(sceneRoot);
			Animator reveal = ViewAnimationUtils.createCircularReveal(sceneRoot, centerX, centerY, startRadius, endRadius);
			reveal.setInterpolator(new BakedBezierInterpolator());
			reveal.setDuration(DURATION);
			return reveal;
		}

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(ObjectAnimator.ofFloat(view, "scaleX", 1, 0)
		                                       .setDuration(DURATION),
		                         ObjectAnimator.ofFloat(view, "scaleY", 1, 0)
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
