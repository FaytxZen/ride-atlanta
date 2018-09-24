package com.andrewvora.apps.rideatlanta.home;

import android.content.Context;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.common.BasePresenter;
import com.andrewvora.apps.rideatlanta.common.BaseView;
import com.andrewvora.apps.rideatlanta.data.contracts.HomeItemModel;

import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public interface HomeContract {

    interface Presenter extends BasePresenter {
        void loadHomeItems();
        void refreshRouteInformation();
		void startPolling();
    }

    interface View extends BaseView<HomeContract.Presenter> {
        Context getViewContext();
        void showLoadingIndicator();
        void hideLoadingIndicator();
        void displayItems(@NonNull List<HomeItemModel> models);
        void updateItems(@NonNull List<HomeItemModel> models);
    }
}
