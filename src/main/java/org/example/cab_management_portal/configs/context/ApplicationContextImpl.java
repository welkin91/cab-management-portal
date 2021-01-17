package org.example.cab_management_portal.configs.context;


import com.google.maps.GeoApiContext;
import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.core.state_machine.core.impl.CabStateMachine;
import org.example.cab_management_portal.core.storage.ServiceStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationContextImpl implements ApplicationContext {

    private GeoApiContext _googleContext;

    @Autowired
    ServiceStorage serviceStorage;

    @Autowired
    CabStateMachine stateMachine;

    @Override
    public void initGoogleClient() {
        log.debug("Initializing Google Distance API client.");

        this._googleContext = new GeoApiContext.Builder()
                .apiKey("AIzaSyAb8ohmBXqtK4y2_a5CFnFnfLGiOsuwjIo")
                .queryRateLimit(10)
                .build();
    }

    @Override
    public void initStorageContext() {
        serviceStorage.init();
    }

    @Override
    public void initStateMachine() {
        stateMachine.configure();
    }

    @Override
    public GeoApiContext getGeoApiContext() {
        return this._googleContext;
    }
}
