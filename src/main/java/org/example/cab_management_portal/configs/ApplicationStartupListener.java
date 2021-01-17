package org.example.cab_management_portal.configs;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.configs.listeners.ApplicationStartupListeners;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationStartupListener {

    @Autowired
    private Environment env;

    @Autowired
    private ApplicationStartupListeners applicationStartupListeners;

    @EventListener
    private void handleContextRefresh(final ContextRefreshedEvent event) {

        try {
            if (event == null) {
                log.error("Received null ContextRefreshedEvent while app startup. Returning without context refresh.");
                return;
            }

            applicationStartupListeners.handleContextRefresh(event);
        } catch (Exception e) {
            log.error("Error while ApplicationStartupListeners.handleContextRefresh(). error: {}", e);
        }
    }
}
