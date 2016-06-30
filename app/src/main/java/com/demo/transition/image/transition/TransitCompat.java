package com.demo.transition.image.transition;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;


public final class TransitCompat {
	/**
	 * There is different between android pre 3.0 and 3.x, 4.x on this wording.
	 */
	private static final String ALPHA = (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) ?
	                                    "alpha" :
	                                    "Alpha";
	private static final int ANIM_DURATION = 600;


	private final ColorDrawable mColorDrawable;

	private Thumbnail mThumbnail;
	private WeakReference<? extends View> mTarget;
	private WeakReference<? extends View> mTransistor;

	private int mLeftDelta;
	private int mTopDelta;
	private float mWidthScale;
	private float mHeightScale;


	public static class Builder {
		private final TransitCompat mTransit;

		public Builder() {
			mTransit = new TransitCompat();
		}


		public Builder setThumbnail(Thumbnail thumbnail) {
			mTransit.mThumbnail = thumbnail;
			return this;
		}

		public Builder setTarget(ImageView target) {
			mTransit.mTarget = new WeakReference<>(target);
			return this;
		}


		public Builder setTransistor(ImageView transistor) {
			mTransit.mTransistor = new WeakReference<>(transistor);
			return this;
		}


		public TransitCompat build(Context cxt) {
			if (mTransit.mTarget.get() == null) {
				return null;
			}
			View targetIv = mTransit.mTarget.get();

			// Figure out where the thumbnail and full size versions are, relative
			// to the screen and each other
			int[] screenLocation = new int[2];
			targetIv.getLocationOnScreen(screenLocation);
			mTransit.mLeftDelta = mTransit.mThumbnail.getLeft() - screenLocation[0];
			mTransit.mTopDelta = mTransit.mThumbnail.getTop() - screenLocation[1];

			// Scale factors to make the large version the same size as the thumbnail
			mTransit.mWidthScale = (float) mTransit.mThumbnail.getWidth() / targetIv.getWidth();
			mTransit.mHeightScale = (float) mTransit.mThumbnail.getHeight() / targetIv.getHeight();

			return mTransit;
		}
	}

	private TransitCompat() {
		mColorDrawable = new ColorDrawable(Color.BLACK);
	}

	/**
	 * The enter animation scales the picture in from its previous thumbnail size/location.
	 *
	 * @param listener For end of animation.
	 */
	public void enter(final ViewPropertyAnimatorListener listener) {
		if (mTarget.get() == null) {
			return;
		}
		View targetIv = mTarget.get();

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			if (mTransistor.get() == null) {
				return;
			}
			View transistorIv = mTransistor.get();

			ViewCompat.setY(transistorIv, mThumbnail.getTop());
			transistorIv.getLayoutParams().width = mThumbnail.getWidth();
			transistorIv.getLayoutParams().height = mThumbnail.getHeight();

			int transistorStartX = targetIv.getWidth()  - mThumbnail.getWidth();
			int transistorStartY = targetIv.getBottom() - mThumbnail.getHeight();
			ViewCompat.setPivotX(targetIv, targetIv.getRight());
			ViewCompat.setPivotY(targetIv, targetIv.getBottom());
			ViewCompat.setScaleX(targetIv, mWidthScale);
			ViewCompat.setScaleY(targetIv, mHeightScale);
			ViewCompat.setTranslationX(targetIv, mLeftDelta);
			ViewCompat.setTranslationY(targetIv, mTopDelta);

			ViewCompat.animate(transistorIv)
			          .setDuration(ANIM_DURATION * 2)
			          .translationX(transistorStartX)
			          .translationY(transistorStartY)
			          .setInterpolator(new BakedBezierInterpolator())
			          .setListener(new ViewPropertyAnimatorListenerAdapter() {
				          @Override
				          public void onAnimationEnd(View view) {
					          if (mTransistor.get() == null) {
						          return;
					          }
					          View transistorIv = mTransistor.get();
					          super.onAnimationEnd(view);
					          ViewCompat.animate(transistorIv)
					                    .alpha(0)
					                    .setDuration(ANIM_DURATION)
					                    .setListener(new ViewPropertyAnimatorListenerAdapter() {
						                    @Override
						                    public void onAnimationStart(View view) {
							                    super.onAnimationEnd(view);
							                    if (mTarget.get() == null) {
								                    return;
							                    }
							                    View targetIv = mTarget.get();
							                    ViewCompat.animate(targetIv)
							                              .setDuration(ANIM_DURATION)
							                              .scaleX(1)
							                              .scaleY(1)
							                              .translationX(0)
							                              .translationY(0)
							                              .setInterpolator(new BakedBezierInterpolator())
							                              .setListener(listener)
							                              .start();
							                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
								                    ObjectAnimator.ofInt(mColorDrawable, ALPHA, 0, 255)
								                                  .setDuration(ANIM_DURATION)
								                                  .start();
							                    }
						                    }
					                    })
					                    .start();
				          }
			          })
			          .start();
		} else {
			listener.onAnimationStart(targetIv);
			listener.onAnimationEnd(targetIv);
			listener.onAnimationCancel(targetIv);
		}
	}


	/**
	 * The exit animation is basically a reverse of the enter animation. This Animate image back to thumbnail
	 * size/location as relieved from bundle.
	 */
	public void exit(final ViewPropertyAnimatorListener listener) {
		if (mTarget.get() == null) {
			return;
		}
		View targetIv = mTarget.get();

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			if (mTransistor.get() == null) {
				return;
			}
			View transistorIv = mTransistor.get();

			ViewCompat.animate(transistorIv)
			          .setDuration(ANIM_DURATION / 2)
			          .alpha(1)
			          .setInterpolator(new BakedBezierInterpolator())
			          .setListener(new ViewPropertyAnimatorListenerAdapter() {
				          @Override
				          public void onAnimationEnd(View view) {
					          if (mTransistor.get() == null) {
						          return;
					          }
					          super.onAnimationEnd(view);
					          View transistorIv = mTransistor.get();
					          ViewCompat.animate(transistorIv)
					                    .translationX(mThumbnail.getLeft())
					                    .translationY(mThumbnail.getTop())
					                    .setInterpolator(new BakedBezierInterpolator())
					                    .setDuration(ANIM_DURATION)
					                    .start();
				          }
			          })
			          .start();

			ViewCompat.setPivotX(targetIv, targetIv.getRight());
			ViewCompat.setPivotY(targetIv, targetIv.getBottom());
			ViewCompat.animate(targetIv)
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
		} else {
			listener.onAnimationStart(targetIv);
			listener.onAnimationEnd(targetIv);
			listener.onAnimationCancel(targetIv);
		}
	}
}
