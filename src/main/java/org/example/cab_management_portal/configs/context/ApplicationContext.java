package org.example.cab_management_portal.configs.context;

import com.google.maps.GeoApiContext;

public interface ApplicationContext {

    public void initGoogleClient();

    public void initStorageContext();

    public void initStateMachine();

    public GeoApiContext getGeoApiContext();
}
