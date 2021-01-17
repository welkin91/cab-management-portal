package org.example.cab_management_portal.startup;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.configs.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationStartupImpl implements ApplicationStartup {

    @Autowired
    ApplicationContext appContext;

    @Override
    public void loadAll() {
        try {
            appContext.initGoogleClient();
            appContext.initStorageContext();
            appContext.initStateMachine();
        }
        catch (Exception e) {
            log.error("error while loading app context. error: {}", e);
        }
    }
}
