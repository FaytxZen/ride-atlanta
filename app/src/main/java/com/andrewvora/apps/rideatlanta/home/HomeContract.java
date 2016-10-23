package com.andrewvora.apps.rideatlanta.home;

import com.andrewvora.apps.rideatlanta.common.BasePresenter;
import com.andrewvora.apps.rideatlanta.common.BaseView;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public interface HomeContract {

    interface Presenter extends BasePresenter {

    }

    interface View extends BaseView<HomeContract.Presenter> {

    }
}
