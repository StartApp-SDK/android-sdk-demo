package com.startapp.startappinterstitialexample;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.startapp.android.publish.ads.nativead.NativeAdDetails;

import java.util.ArrayList;
import java.util.Arrays;

public class NativeAdsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_ITEM_TYPE = 0;
    private static final int VIEW_AD_TYPE = 1;

    private ArrayList<NativeAdDetails> mNativeAdsList = new ArrayList<>();
    private ArrayList<Integer> mResIdsList = new ArrayList<>(
            Arrays.asList(
                    R.drawable.user_image1,
                    R.drawable.user_image2,
                    R.drawable.user_image3));


    public void setItems(ArrayList<NativeAdDetails> adsList) {
        mNativeAdsList = adsList;
        notifyDataSetChanged();
    }

    private int getInnerPosition(int position) {
        final int lastResId = mResIdsList.size() - 1;
        final int last = mNativeAdsList.size() + lastResId;

        if (position == 0) return 0;
        if (position == last) return lastResId;
        if (position == last / 2) return lastResId / 2;

        if (position < last / 2) return position - 1;
        return position - 2;
    }

    @Override
    public int getItemViewType(int position) {
        int last = mNativeAdsList.size() + mResIdsList.size() - 1;
        if (position ==  0 || position == last || position == last / 2) {
            return VIEW_ITEM_TYPE;
        }

        return VIEW_AD_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM_TYPE) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_data_row, parent, false);
            return new ImageHolder(view);
        }

        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_ad_row, parent, false);
        return new NativeAdHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_ITEM_TYPE) {
            final ImageHolder imageHolder = (ImageHolder) holder;
            imageHolder.mImageView.setImageResource(mResIdsList.get(getInnerPosition(position)));
        } else {
            final NativeAdHolder nativeAdHolder = (NativeAdHolder) holder;
            nativeAdHolder.bindView(mNativeAdsList.get(getInnerPosition(position)));
        }
    }

    @Override
    public int getItemCount() {
        return mResIdsList.size() + mNativeAdsList.size();
    }

    private static class ImageHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        private ImageHolder(View view) {
            super(view);

            mImageView = view.findViewById(R.id.ivAppData);
        }
    }

    private static class NativeAdHolder extends RecyclerView.ViewHolder {

        private LinearLayout mContainer;
        private ImageView mIcon;
        private TextView mTitle;
        private TextView mDescription;
        private TextView mImageUrl;

        private NativeAdHolder(View view) {
            super(view);

            mContainer = view.findViewById(R.id.container);
            mIcon = view.findViewById(R.id.ivIcon);
            mTitle = view.findViewById(R.id.tvTitle);
            mDescription = view.findViewById(R.id.tvDescription);
            mImageUrl = view.findViewById(R.id.tvImageUrl);
        }

        private void bindView(@NonNull NativeAdDetails ad) {
            mIcon.setImageBitmap(ad.getImageBitmap());
            mTitle.setText(ad.getTitle());
            mDescription.setText(ad.getDescription());
            mImageUrl.setText(ad.getImageUrl());

            ad.registerViewForInteraction(mContainer);
        }
    }
}
