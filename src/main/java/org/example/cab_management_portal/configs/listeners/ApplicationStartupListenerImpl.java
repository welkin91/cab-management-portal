package org.example.cab_management_portal.configs.listeners;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.startup.ApplicationStartup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationStartupListenerImpl implements ApplicationStartupListeners {

    @Autowired
    private ApplicationStartup applicationStartup;

    @Override
    public void handleContextRefresh(ContextRefreshedEvent event) {
        log.debug("Loading Internal App Data.");
        applicationStartup.loadAll();
    }
}
