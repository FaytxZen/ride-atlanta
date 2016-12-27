package com.andrewvora.apps.rideatlanta.trains;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.common.models.Train;

import java.util.ArrayList;
import java.util.List;

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
    @Nullable private TrainRoutesFragment.TrainItemListener mItemListener;

    public TrainRoutesAdapter(@Nullable List<Train> trainList,
                              @Nullable TrainRoutesFragment.TrainItemListener listener)
    {
        mTrainList = trainList == null ? new ArrayList<Train>() : trainList;
        mItemListener  = listener;
    }

    @Override
    public TrainRoutesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_train_route, parent, false);

        return new TrainRoutesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrainRoutesViewHolder holder, int position) {
        Train train = mTrainList.get(position);

        holder.lineTextView.setText(train.getLine());

        // determine train destination
        String destinationText = train.getStation();
        holder.destinationTextView.setText(destinationText);

        // determine train status
        holder.statusTextView.setText(train.getWaitingTime());
    }

    @Override
    public int getItemCount() {
        return mTrainList.size();
    }

    public void setTrains(@NonNull List<Train> trains) {
        mTrainList = trains;
    }

    static class TrainRoutesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.train_destination_text_view) TextView destinationTextView;
        @BindView(R.id.train_line_text_view) TextView lineTextView;
        @BindView(R.id.train_status_text_view) TextView statusTextView;

        TrainRoutesViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
