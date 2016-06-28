package com.demo.transition.image.transition.v21;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.AttributeSet;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public final class PlatformTransition extends TransitionSet {
	public PlatformTransition() {
		init();
	}

	/**
	 * This constructor allows us to use this transition in XML
	 */
	public PlatformTransition(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setOrdering(ORDERING_TOGETHER);
		addTransition(new ChangeBounds()).addTransition(new ChangeTransform())
		                                 .addTransition(new ChangeImageTransform());
	}
}
