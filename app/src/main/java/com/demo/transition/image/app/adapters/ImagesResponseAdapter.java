package com.demo.transition.image.app.adapters;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.demo.transition.image.R;
import com.demo.transition.image.bus.ClickImageEvent;
import com.demo.transition.image.databinding.ListImagesItemBinding;
import com.demo.transition.image.ds.Image;
import com.demo.transition.image.ds.ImagesResponse;
import com.demo.transition.image.transition.Thumbnail;

import org.greenrobot.eventbus.EventBus;

public final class ImagesResponseAdapter extends RecyclerView.Adapter<ImagesResponseAdapter.ImagesListItemViewHolder> {
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_list_images;
	private final ImagesResponse mImagesResponse;


	public ImagesResponseAdapter(ImagesResponse imagesResponse) {
		mImagesResponse = imagesResponse;
	}


	@Override
	public int getItemCount() {
		return mImagesResponse == null ?
		       0 :
		       mImagesResponse.getResult()
		                      .size();
	}

	@Override
	public ImagesListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context cxt = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(cxt);
		ListImagesItemBinding binding = DataBindingUtil.inflate(inflater, ITEM_LAYOUT, parent, false);
		return new ImagesListItemViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(ImagesListItemViewHolder holder, int position) {
		holder.mBinding.setHandler(new ImagesListItemHandler(holder.mBinding));
		holder.mBinding.setImage(mImagesResponse.getResult()
		                                        .get(position));
		holder.mBinding.executePendingBindings();
	}

	static class ImagesListItemViewHolder extends RecyclerView.ViewHolder {
		private final ListImagesItemBinding mBinding;

		ImagesListItemViewHolder(ListImagesItemBinding binding) {
			super(binding.getRoot());
			mBinding = binding;
		}
	}

	public static class ImagesListItemHandler {
		private final ListImagesItemBinding mBinding;

		ImagesListItemHandler(ListImagesItemBinding binding) {
			mBinding = binding;
		}

		@SuppressWarnings("unused")
		public void onImageItemClick(View v) {
			Thumbnail thumbnail = null;

			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB && android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
				ImageView imageView = (ImageView) v.findViewById(R.id.thumbnail_iv);
				int[] screenLocation = new int[2];
				imageView.getLocationOnScreen(screenLocation);
				thumbnail = new Thumbnail(screenLocation[1], screenLocation[0], imageView.getWidth(), imageView.getHeight());
			}
			Image image = mBinding.getImage();
			EventBus.getDefault()
			        .post(new ClickImageEvent(image, thumbnail));
		}
	}
}
