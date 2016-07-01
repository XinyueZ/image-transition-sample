package com.demo.transition.image.transition;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public final class TransitCompat {
	/**
	 * There is different between android pre 3.0 and 3.x, 4.x on this wording.
	 */
	private static final String ALPHA = (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) ?
	                                    "alpha" :
	                                    "Alpha";
	public static final int ANIM_DURATION = 600;


	private List<WeakReference<?>> mObjectsToFade;
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

		public Builder setTarget(ImageView target) {
			mTransit.mTarget = new WeakReference<>(target);
			return this;
		}


		public Builder setObjectsToFade(Object... objects) {
			mTransit.mObjectsToFade = new ArrayList<>(objects.length);
			for (Object obj : objects) {
				mTransit.mObjectsToFade.add(new WeakReference<>(obj));
			}
			return this;
		}

		public Builder setPlayTogetherAfterEnterValueAnimators(ValueAnimatorCompat... playerAnimators) {
			mTransit.mPlayTogetherAfterEnterValueAnimators = Arrays.asList(playerAnimators);
			return this;
		}

		public Builder setPlayTogetherBeforeExitValueAnimators(ValueAnimatorCompat... playerAnimators) {
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

	/**
	 * The enter animation scales the picture in from its previous thumbnail size/location.
	 *
	 * @param listener For end of animation.
	 */
	public void enter(final ViewPropertyAnimatorListener listener) {
		if (mTarget.get() == null) {
			return;
		}
		final View targetIv = mTarget.get();
		final Interpolator interpolator = new BakedBezierInterpolator();
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && mObjectsToFade != null) {
				List<ObjectAnimator> animators = new ArrayList<>(mObjectsToFade.size());
				AnimatorSet animatorSet = new AnimatorSet();
				for (WeakReference<?> obj : mObjectsToFade) {
					if (obj.get() != null) {
						animators.add(ObjectAnimator.ofInt(obj.get(), ALPHA, 0, 225)
						                            .setDuration(ANIM_DURATION / 2));
					}
				}
				ObjectAnimator[] array = new ObjectAnimator[animators.size()];
				animators.toArray(array);
				animatorSet.setInterpolator(interpolator);
				animatorSet.playTogether(array);
				animatorSet.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						super.onAnimationEnd(animation);
						if (mTarget.get() == null) {
							return;
						}
						View targetIv = mTarget.get();
						animateEnterTarget(listener, targetIv, new BakedBezierInterpolator());
					}
				});
				animatorSet.start();
			} else {
				animateEnterTarget(listener, targetIv, interpolator);
			}
		} else {
			if(listener != null) {
				listener.onAnimationStart(targetIv);
				listener.onAnimationEnd(targetIv);
				listener.onAnimationCancel(targetIv);
			}
		}
	}

	private void animateEnterTarget(final ViewPropertyAnimatorListener listener, View targetIv, Interpolator interpolator) {
		ViewCompat.setPivotX(targetIv, 0);
		ViewCompat.setPivotY(targetIv, 0);
		ViewCompat.setScaleX(targetIv, mWidthScale);
		ViewCompat.setScaleY(targetIv, mHeightScale);
		ViewCompat.setTranslationX(targetIv, mLeftDelta);
		ViewCompat.setTranslationY(targetIv, mTopDelta);

		ViewCompat.animate(targetIv)
		          .setDuration(ANIM_DURATION)
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
				          for (ValueAnimatorCompat p : mPlayTogetherAfterEnterValueAnimators) {
					          p.start();
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


	/**
	 * The exit animation is basically a reverse of the enter animation. This Animate image back to thumbnail
	 * size/location as relieved from bundle.
	 */
	public void exit(final ViewPropertyAnimatorListener listener) {
		if (mTarget.get() == null) {
			return;
		}
		final View targetIv = mTarget.get();
		final Interpolator interpolator = new BakedBezierInterpolator();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && mObjectsToFade != null) {
				List<ObjectAnimator> animators = new ArrayList<>(mObjectsToFade.size());
				AnimatorSet animatorSet = new AnimatorSet();
				for (WeakReference<?> obj : mObjectsToFade) {
					if (obj.get() != null) {
						animators.add(ObjectAnimator.ofInt(obj.get(), ALPHA, 0)
						                            .setDuration(ANIM_DURATION / 2));
					}
				}
				ObjectAnimator[] array = new ObjectAnimator[animators.size()];
				animators.toArray(array);
				animatorSet.setInterpolator(interpolator);
				animatorSet.playTogether(array);
				animatorSet.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						super.onAnimationEnd(animation);
						if (mTarget.get() == null) {
							return;
						}
						View targetIv = mTarget.get();
						animateExitTarget(listener, targetIv, new BakedBezierInterpolator());
					}
				});
				animatorSet.start();
			} else {
				animateExitTarget(listener, targetIv, interpolator);
			}
		} else {
			if(listener != null) {
				listener.onAnimationStart(targetIv);
				listener.onAnimationEnd(targetIv);
				listener.onAnimationCancel(targetIv);
			}
		}
	}

	private void animateExitTarget(final ViewPropertyAnimatorListener listener, View targetIv, Interpolator interpolator) {
		ViewCompat.animate(targetIv)
		          .setDuration(ANIM_DURATION * 2)
		          .scaleX(mWidthScale)
		          .scaleY(mHeightScale)
		          .translationX(mLeftDelta)
		          .translationY(mTopDelta)
		          .setInterpolator(interpolator)
		          .setListener(new ViewPropertyAnimatorListenerAdapter() {
			          @Override
			          public void onAnimationStart(View view) {
				          super.onAnimationStart(view);
				          for (ValueAnimatorCompat p : mPlayTogetherBeforeExitValueAnimators) {
					          p.start();
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
		          })
		          .start();
	}
}
