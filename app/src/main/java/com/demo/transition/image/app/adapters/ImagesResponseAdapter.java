package com.demo.transition.image.app.adapters;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
	private  ImagesResponse mImagesResponse;



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
		holder.mBinding.setHandler(new ImagesListItemHandler(holder));
		holder.mBinding.setImage(mImagesResponse.getResult()
		                                        .get(holder.getAdapterPosition()));
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
		private final ImagesListItemViewHolder mHolder;


		ImagesListItemHandler(ImagesListItemViewHolder holder) {
			mHolder = holder;
		}

		@SuppressWarnings("unused")
		public void onImageItemClick(View v) {
			ListImagesItemBinding binding = mHolder.mBinding;
			Image image = binding.getImage();

			int[] screenLocation = new int[2];
			binding.thumbnailIv.getLocationOnScreen(screenLocation);
			Thumbnail thumbnail = new Thumbnail(screenLocation[1], screenLocation[0], binding.thumbnailIv.getWidth(), binding.thumbnailIv.getHeight());
			ViewCompat.setTransitionName(binding.thumbnailIv, "image_" + mHolder.getAdapterPosition());
			EventBus.getDefault()
			        .post(new ClickImageEvent(image, thumbnail, binding.thumbnailIv));
		}
	}
}
