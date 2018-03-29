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
import com.udacity.popularmoviesstage2.valueobjects.ReviewInfo;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder>  {

    private final Context mContext;
    private List<ReviewInfo> reviewInfos;

    public ReviewsAdapter(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_reviews, viewGroup, false);
        view.setFocusable(true);

        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder reviewsAdapterViewHolder, int position) {
        ReviewInfo reviewInfo = reviewInfos.get(position);
        reviewsAdapterViewHolder.tvReviewAuthor.setText(reviewInfo.author);
        reviewsAdapterViewHolder.tvReviewContent.setText(reviewInfo.content);
        if (position < (reviewInfos.size() - 1)) {
            reviewsAdapterViewHolder.ivDivider.setVisibility(View.VISIBLE);
        } else {
            reviewsAdapterViewHolder.ivDivider.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (null == reviewInfos) return 0;
        return reviewInfos.size();
    }

    /*@Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_REVIEWS;
    }*/

    public void reloadData(List<ReviewInfo> newData) {
        reviewInfos = newData;
        notifyDataSetChanged();
    }

    class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewAuthor;
        TextView tvReviewContent;
        ImageView ivDivider;

        ReviewsAdapterViewHolder(View view) {
            super(view);
            tvReviewAuthor = view.findViewById(R.id.textview_review_author);
            tvReviewContent = view.findViewById(R.id.textview_review_content);
            ivDivider = view.findViewById(R.id.divider_review);
        }
    }
}
