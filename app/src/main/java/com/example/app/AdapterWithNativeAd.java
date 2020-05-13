package com.example.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.startapp.sdk.ads.nativead.NativeAdDetails;

import java.util.List;

class AdapterWithNativeAd extends RecyclerView.Adapter<AdapterWithNativeAd.BaseViewHolder> {
    private static final int TYPE_NATIVE_AD = 0;
    private static final int TYPE_DATA = 1;

    @NonNull
    private final Context context;

    @Nullable
    private List<NativeAdDetails> nativeAd;

    @Nullable
    private List<String> data;

    public void setNativeAd(@Nullable List<NativeAdDetails> nativeAd) {
        this.nativeAd = nativeAd;

        notifyDataSetChanged();
    }

    public void setData(@Nullable List<String> data) {
        this.data = data;

        notifyDataSetChanged();
    }

    public AdapterWithNativeAd(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        int result = 0;

        if (nativeAd != null) {
            result += nativeAd.size();
        }

        if (data != null) {
            result += data.size();
        }

        return result;
    }

    @Nullable
    private Object getItem(int position) {
        int nativeAdOffset = 0;

        if (nativeAd != null) {
            nativeAdOffset = nativeAd.size();

            if (position < nativeAdOffset) {
                return nativeAd.get(position);
            }
        }

        if (data != null) {
            return data.get(position - nativeAdOffset);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (nativeAd != null) {
            if (position < nativeAd.size()) {
                return TYPE_NATIVE_AD;
            }
        }

        return TYPE_DATA;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_NATIVE_AD) {
            View view = LayoutInflater.from(context).inflate(R.layout.native_ad_item, parent, false);
            return new NativeAdHolder(view);
        } else if (viewType == TYPE_DATA) {
            return new DataViewHolder(new TextView(context));
        } else {
            // NOTE this case is impossible
            return new BaseViewHolder(new View(context));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder {
        BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void onBind(@Nullable Object item) {
            // none
        }
    }

    static class NativeAdHolder extends BaseViewHolder {
        ImageView icon;
        TextView title;
        TextView description;
        Button button;

        private NativeAdHolder(View view) {
            super(view);

            icon = view.findViewById(R.id.icon);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
            button = view.findViewById(R.id.button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.performClick();
                }
            });
        }

        void onBind(@Nullable Object item) {
            NativeAdDetails ad = (NativeAdDetails) item;

            if (ad != null) {
                icon.setImageBitmap(ad.getImageBitmap());
                title.setText(ad.getTitle());
                description.setText(ad.getDescription());
                button.setText(ad.isApp() ? "Install" : "Open");

                ad.registerViewForInteraction(itemView);
            }
        }
    }

    static class DataViewHolder extends BaseViewHolder {
        private final TextView textView;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = (TextView) itemView;
        }

        @Override
        void onBind(@Nullable Object item) {
            String string = (String) item;
            textView.setText(string);
        }
    }
}
