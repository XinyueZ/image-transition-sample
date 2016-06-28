package com.demo.transition.image.transition;


import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public final class TransitCompat {
	/**
	 * There is different between android pre 3.0 and 3.x, 4.x on this wording.
	 */
	public static final String ALPHA = (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) ?
	                                   "alpha" :
	                                   "Alpha";
	private static final int ANIM_DURATION = 600;


	private ColorDrawable mColorDrawable;

	private Thumbnail mThumbnail;
	private ImageView mTarget;
	private ImageView mTransistor;

	private int mLeftDelta;
	private int mTopDelta;
	private float mWidthScale;
	private float mHeightScale;

	public static class Builder {
		private TransitCompat mTransition;

		public Builder() {
			mTransition = new TransitCompat();
		}


		public Builder setThumbnail(Thumbnail thumbnail) {
			mTransition.mThumbnail = thumbnail;
			return this;
		}

		public Builder setTarget(ImageView target) {
			mTransition.mTarget = target;
			return this;
		}


		public Builder setTransistor(ImageView transistor) {
			mTransition.mTransistor = transistor;
			return this;
		}


		public TransitCompat build(Context cxt) {
			Glide.with(cxt)
			     .load(mTransition.mThumbnail.getSource())
			     .diskCacheStrategy(DiskCacheStrategy.ALL)
			     .into(mTransition.mTarget);

			// Figure out where the thumbnail and full size versions are, relative
			// to the screen and each other
			int[] screenLocation = new int[2];
			mTransition.mTarget.getLocationOnScreen(screenLocation);
			mTransition.mLeftDelta = mTransition.mThumbnail.getLeft() - screenLocation[0];
			mTransition.mTopDelta = mTransition.mThumbnail.getTop() - screenLocation[1];

			// Scale factors to make the large version the same size as the thumbnail
			mTransition.mWidthScale = (float) mTransition.mThumbnail.getWidth() / mTransition.mTarget.getWidth();
			mTransition.mHeightScale = (float) mTransition.mThumbnail.getHeight() / mTransition.mTarget.getHeight();

			return mTransition;
		}
	}

	public TransitCompat() {
		mColorDrawable = new ColorDrawable(Color.BLACK);
	}

	/**
	 * The enter animation scales the picture in from its previous thumbnail size/location.
	 *
	 * @param listener For end of animation.
	 */
	public void enter(final ViewPropertyAnimatorListener listener) {
		ViewCompat.setY(mTransistor, mThumbnail.getTop());
		mTransistor.getLayoutParams().width = mThumbnail.getWidth();
		mTransistor.getLayoutParams().height = mThumbnail.getHeight();

		int transistorStartX = mTarget.getWidth() - mThumbnail.getWidth();
		int transistorStartY = mTarget.getBottom() - mThumbnail.getHeight();
		ViewCompat.setPivotX(mTarget, mTarget.getWidth());
		ViewCompat.setPivotY(mTarget, mTarget.getBottom());
		ViewCompat.setScaleX(mTarget, mWidthScale);
		ViewCompat.setScaleY(mTarget, mHeightScale);
		ViewCompat.setTranslationX(mTarget, mLeftDelta);
		ViewCompat.setTranslationY(mTarget, mTopDelta);

		ViewCompat.animate(mTransistor)
		          .setDuration(ANIM_DURATION * 2)
		          .translationX(transistorStartX)
		          .translationY(transistorStartY)
		          .setInterpolator(new BakedBezierInterpolator())
		          .setListener(new ViewPropertyAnimatorListenerAdapter() {
			          @Override
			          public void onAnimationEnd(View view) {
				          ViewCompat.animate(mTransistor)
				                    .alpha(0)
				                    .setDuration(ANIM_DURATION)
				                    .start();
				          ViewCompat.animate(mTarget)
				                    .setDuration(ANIM_DURATION)
				                    .scaleX(1)
				                    .scaleY(1)
				                    .translationX(0)
				                    .translationY(0)
				                    .setInterpolator(new BakedBezierInterpolator())
				                    .setListener(listener)
				                    .start();
				          ObjectAnimator.ofInt(mColorDrawable, ALPHA, 0, 255)
				                        .setDuration(ANIM_DURATION)
				                        .start();
			          }
		          })
		          .start();
	}


	/**
	 * The exit animation is basically a reverse of the enter animation. This Animate image back to thumbnail
	 * size/location as relieved from bundle.
	 */
	public void exit(final ViewPropertyAnimatorListener listener) {
		ViewCompat.animate(mTransistor)
		          .setDuration(ANIM_DURATION / 2)
		          .alpha(1)
		          .setInterpolator(new BakedBezierInterpolator())
		          .setListener(new ViewPropertyAnimatorListenerAdapter() {
			          @Override
			          public void onAnimationEnd(View view) {
				          super.onAnimationEnd(view);
				          ViewCompat.animate(mTransistor)
				                    .translationX(mThumbnail.getLeft())
				                    .translationY(mThumbnail.getTop())
				                    .setInterpolator(new BakedBezierInterpolator())
				                    .setDuration(ANIM_DURATION).start();
			          }
		          })
		          .start();

		ViewCompat.setPivotX(mTarget, mTarget.getRight());
		ViewCompat.setPivotY(mTarget, mTarget.getBottom());
		ViewCompat.animate(mTarget)
		          .setDuration(ANIM_DURATION * 2)
		          .scaleX(mWidthScale)
		          .scaleY(mHeightScale)
		          .translationX(mLeftDelta)
		          .translationY(mTopDelta)
		          .setInterpolator(new BakedBezierInterpolator())
		          .setListener(listener)
		          .start();


		ObjectAnimator.ofInt(mColorDrawable, ALPHA, 0)
		              .setDuration(ANIM_DURATION)
		              .start();
	}
}
