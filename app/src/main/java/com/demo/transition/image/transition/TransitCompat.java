package com.demo.transition.image.transition;


import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.animation.AnimatorUpdateListenerCompat;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.View;
import android.view.animation.Interpolator;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;


public final class TransitCompat {
	public static final int ANIM_DURATION = 600;


	private List<ValueAnimatorCompat> mPlayTogetherAfterEnterValueAnimators;
	private List<ValueAnimatorCompat> mPlayTogetherBeforeExitValueAnimators;

	private Thumbnail mThumbnail;
	private WeakReference<? extends View> mTarget;

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

		public Builder setTarget(View target) {
			mTransit.mTarget = new WeakReference<>(target);
			return this;
		}


		public Builder setPlayTogetherAfterEnterTransition(ValueAnimatorCompat... playerAnimators) {
			mTransit.mPlayTogetherAfterEnterValueAnimators = Arrays.asList(playerAnimators);
			return this;
		}

		public Builder setPlayTogetherBeforeExitTransition(ValueAnimatorCompat... playerAnimators) {
			mTransit.mPlayTogetherBeforeExitValueAnimators = Arrays.asList(playerAnimators);
			return this;
		}

		public TransitCompat build() {
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
	}

	public void enter(final ViewPropertyAnimatorListener listener) {
		if (mTarget.get() == null) {
			return;
		}
		final Interpolator interpolator = new BakedBezierInterpolator();
		animateEnterTarget(listener,  interpolator);
	}

	private void animateEnterTarget(final ViewPropertyAnimatorListener listener,  Interpolator interpolator) {
		View target = mTarget.get();
		if (target == null) {
			listener.onAnimationEnd(null);
			return;
		}
		ViewCompat.setPivotX(target, 0);
		ViewCompat.setPivotY(target, 0);
		ViewCompat.setScaleX(target, mWidthScale);
		ViewCompat.setScaleY(target, mHeightScale);
		ViewCompat.setTranslationX(target, mLeftDelta);
		ViewCompat.setTranslationY(target, mTopDelta);

		ViewCompat.animate(target)
		          .setDuration(ANIM_DURATION * 4)
		          .scaleX(1)
		          .scaleY(1)
		          .translationX(0)
		          .translationY(0)
		          .setInterpolator(interpolator)
		          .setListener(new ViewPropertyAnimatorListenerAdapter() {
			          @Override
			          public void onAnimationStart(View view) {
				          super.onAnimationStart(view);
				          listener.onAnimationStart(view);
			          }

			          @Override
			          public void onAnimationEnd(View view) {
				          super.onAnimationEnd(view);
				          listener.onAnimationEnd(view);
				          if (mPlayTogetherAfterEnterValueAnimators != null) {
					          for (ValueAnimatorCompat p : mPlayTogetherAfterEnterValueAnimators) {
						          p.start();
					          }
				          }
			          }

			          @Override
			          public void onAnimationCancel(View view) {
				          super.onAnimationCancel(view);
				          listener.onAnimationCancel(view);
			          }
		          })
		          .start();
	}



	public void exit(final ViewPropertyAnimatorListener listener) {
		if (mTarget.get() == null) {
			return;
		}
		final Interpolator interpolator = new BakedBezierInterpolator();
		animateExitTarget(listener, interpolator);
	}

	private void animateExitTarget(final ViewPropertyAnimatorListener listener, Interpolator interpolator) {
		View target = mTarget.get();
		if (target == null) {
			listener.onAnimationStart(null);
			return;
		}

		ViewCompat.animate(target)
		          .setDuration(ANIM_DURATION * 2)
		          .translationX(mLeftDelta)
		          .translationY(mTopDelta)
		          .setInterpolator(interpolator)
		          .setListener(new ViewPropertyAnimatorListenerAdapter() {
			          @Override
			          public void onAnimationStart(View view) {
				          super.onAnimationStart(view);
				          if (mPlayTogetherBeforeExitValueAnimators != null) {
					          for (ValueAnimatorCompat p : mPlayTogetherBeforeExitValueAnimators) {
						          p.start();
					          }
				          }
				          listener.onAnimationStart(view);
			          }

			          @Override
			          public void onAnimationEnd(View view) {
				          super.onAnimationEnd(view);
				          listener.onAnimationEnd(view);
			          }

			          @Override
			          public void onAnimationCancel(View view) {
				          super.onAnimationCancel(view);
				          listener.onAnimationCancel(view);
			          }
		          }).start();

		ValueAnimatorCompat exitAnimator = AnimatorCompatHelper.emptyValueAnimator();
		exitAnimator.setDuration(ANIM_DURATION * 2);
		final int viewWidth = target.getWidth();
		final int viewHeight = target.getHeight();
		exitAnimator.addUpdateListener(new AnimatorUpdateListenerCompat() {
			private float oldWidth = viewWidth;
			private float endWidth = 0;
			private float oldHeight = viewHeight;
			private float endHeight = 0;

			private Interpolator interpolator2 = new BakedBezierInterpolator();

			@Override
			public void onAnimationUpdate(ValueAnimatorCompat animation) {
				View target = mTarget.get();
				if (target == null) {
					return;
				}
				float fraction = interpolator2.getInterpolation(animation.getAnimatedFraction());

				float width = oldWidth + (fraction * (endWidth - oldWidth));
				target.getLayoutParams().width = (int) width;
				float height = oldHeight + (fraction * (endHeight - oldHeight));
				target.getLayoutParams().height = (int) height;

				target.requestLayout();
			}
		});
		exitAnimator.start();
	}
}
