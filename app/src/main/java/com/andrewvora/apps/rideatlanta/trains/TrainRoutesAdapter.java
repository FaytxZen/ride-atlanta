package com.andrewvora.apps.rideatlanta.trains;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.utils.WordUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faytx on 11/10/2016.
 * @author Andrew Vorakrajangthiti
 */
public class TrainRoutesAdapter extends
        RecyclerView.Adapter<TrainRoutesAdapter.TrainRoutesViewHolder>
{
    @NonNull private List<Train> mTrainList;
    @NonNull private Set<String> mFavoritedRouteIds;
    @Nullable private TrainRoutesFragment.TrainItemListener mItemListener;

    TrainRoutesAdapter(@NonNull List<Train> trainList,
					   @Nullable TrainRoutesFragment.TrainItemListener listener)
    {
        mTrainList = trainList;
        mItemListener  = listener;
        mFavoritedRouteIds = new HashSet<>();
    }

    @Override
    public TrainRoutesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_train_route, parent, false);

        return new TrainRoutesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrainRoutesViewHolder holder, int position) {
        Train train = mTrainList.get(position);

        configureTrainLineView(holder.lineTextView, train);

        // determine train destination
        String destinationText = WordUtils.capitalizeWords(train.getStation());
        holder.destinationTextView.setText(destinationText);

        // determine travel direction
        holder.directionTextView.setText(train.getDirection());

        // determine train status
        holder.statusTextView.setText(train.getWaitingTime());

        // attach click listeners
        String key = train.getFavoriteRouteKey();
        holder.favoriteButton.setSelected(mFavoritedRouteIds.contains(key));
        train.setFavorited(mFavoritedRouteIds.contains(key));

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mItemListener != null) {
                    mItemListener.onFavoriteItem(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrainList.size();
    }

    void setFavoritedRouteIds(@NonNull Set<String> favRouteIds) {
        mFavoritedRouteIds = favRouteIds;
    }

    Set<String> getFavoriteRouteIds() {
        return mFavoritedRouteIds;
    }

    public void setTrains(@NonNull List<Train> trains) {
        mTrainList = trains;
    }

    public List<Train> getTrains() {
        return mTrainList;
    }

    Train getTrain(int position) {
        return mTrainList.get(position);
    }

    private void configureTrainLineView(@NonNull TextView lineTextView, @NonNull Train train) {
        final int colorResId = Train.getColorRes(train.getLine());

        Context context = lineTextView.getContext();
        lineTextView.setBackgroundColor(ContextCompat.getColor(context, colorResId));
        lineTextView.setText(train.getLine());
    }

    static class TrainRoutesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.train_destination_text_view) TextView destinationTextView;
        @BindView(R.id.train_line_text_view) TextView lineTextView;
        @BindView(R.id.train_status_text_view) TextView statusTextView;
        @BindView(R.id.train_direction) TextView directionTextView;
        @BindView(R.id.train_favorite_button) ImageView favoriteButton;

        TrainRoutesViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
