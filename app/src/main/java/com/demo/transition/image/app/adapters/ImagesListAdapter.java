package com.demo.transition.image.app.adapters;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demo.transition.image.R;
import com.demo.transition.image.bus.ClickImageEvent;
import com.demo.transition.image.databinding.ListImagesItemBinding;
import com.demo.transition.image.ds.Image;
import com.demo.transition.image.ds.ImagesResponse;

import org.greenrobot.eventbus.EventBus;

public final class ImagesListAdapter extends RecyclerView.Adapter<ImagesListAdapter.ImagesListItemViewHolder> {
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_list_images;
	private final ImagesResponse mImagesResponse;


	public ImagesListAdapter(ImagesResponse imagesResponse) {
		mImagesResponse = imagesResponse;
	}


	@Override
	public int getItemCount() {
		return mImagesResponse.getResult()
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
		holder.mBinding.setHandler(new ImagesListItemHandler());
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
		@SuppressWarnings("unused")
		public void onImageItemClick(View v) {
			Image image = (Image) v.getTag();
			EventBus.getDefault()
			        .post(new ClickImageEvent(image));
		}
	}
}
