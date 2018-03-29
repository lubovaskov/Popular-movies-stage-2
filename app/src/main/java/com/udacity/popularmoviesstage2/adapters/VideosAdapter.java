package com.udacity.popularmoviesstage2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.popularmoviesstage2.R;
import com.udacity.popularmoviesstage2.valueobjects.VideoInfo;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosAdapterViewHolder> {

    private final Context mContext;
    private List<VideoInfo> videoInfos;

    final private VideosAdapterOnClickHandler mClickHandler;

    public interface VideosAdapterOnClickHandler {
        void onVideoClick(String videoKey);
    }

    public VideosAdapter(@NonNull Context context, VideosAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public VideosAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_videos, viewGroup, false);
        view.setFocusable(true);

        return new VideosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideosAdapterViewHolder videosAdapterViewHolder, int position) {
        VideoInfo videoInfo = videoInfos.get(position);
        videosAdapterViewHolder.tvVideoTitle.setText(videoInfo.name);
        if (position < (videoInfos.size() - 1)) {
            videosAdapterViewHolder.ivDivider.setVisibility(View.VISIBLE);
        } else {
            videosAdapterViewHolder.ivDivider.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        if (null == videoInfos) return 0;
        return videoInfos.size();
    }

    /*@Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_VIDEOS;
    }*/

    public void reloadData(List<VideoInfo> newData) {
        videoInfos = newData;
        notifyDataSetChanged();
    }

    class VideosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvVideoTitle;
        ImageView ivDivider;

        VideosAdapterViewHolder(View view) {
            super(view);
            tvVideoTitle = view.findViewById(R.id.textview_video_title);
            ivDivider = view.findViewById(R.id.divider_video);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String videoKey = videoInfos.get(adapterPosition).key;
            mClickHandler.onVideoClick(videoKey);
        }
    }
}
