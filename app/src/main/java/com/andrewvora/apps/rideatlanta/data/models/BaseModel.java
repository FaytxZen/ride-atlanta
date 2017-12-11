package com.andrewvora.apps.rideatlanta.data.models;

/**
 * Created by faytx on 11/13/2016.
 * @author Andrew Vorakrajangthiti
 */

public abstract class BaseModel {

    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
