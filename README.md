image-transition-sample
====

## Learning how to make transitions on images, objects to show "list"-"detail".

One of the cornerstones of Material Design is meaningful motion between screens. Lollipop introduced support for these animations in the form of the transitions framework, which allows us to animate transitions between Activities and Fragments.

However under Lollipop we should do some tricks to make it "compat".

#### Project with sampe codes, you can use what worth.

My end product is fairly simple. We will be making an app that has a list of images with text, and when you click on an image the app will show a details screen. On Lollipop and above thanks to the transitions framework, the image from the list will animate into place on the details screen.

Under Lollipop we do followings:

- "thumbnail" for meta-data from image-view on "list", inc. "left", "top", "width" and "height".

- Above Lollipop the sample uses [Android transition framework](https://developer.android.com/training/transitions/index.html), because of limited build-in transitions but remember that the framework has been supported since Android 4.4.2 (API level 19) or higher.

- Below Lollipop the sample uses ```TransitCompat```

- The "detail" can be shown by activity or fragment, you can select from menu on home-screen.


## Code

```java
	//Start transition
	mTransition = new TransitCompat.Builder(App.Instance).setThumbnail((Thumbnail) object)
					                     .setTarget(mBinding.imageIv)//The target that will be mirrored from thumbnail
					                     .setTransistor(mBinding.tempIv)//The helper image-view as "transistor".
					                     .build(activity);
					mTransition.enter(new ViewPropertyAnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(View view) {
							super.onAnimationEnd(view);
							//Some UI makeup when whole transition starting.
						}
					});

	//End of it
	@Override
	public void onBackPressed() {
	if (mTransition != null) {
		mTransition.exit(new ViewPropertyAnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(View view) {
				super.onAnimationStart(view);
				//Some UI makeup when whole transition ending.
			}

			@Override
			public void onAnimationEnd(View v) {
				super.onAnimationEnd(v);
				ActivityCompat.finishAfterTransition(DetailActivity.this);
			}
		});
	} else {
		super.onBackPressed();
	}
	}

```


## License

```

The MIT License (MIT)

Copyright (c) 2016 Chris Xinyue Zhao

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


```
