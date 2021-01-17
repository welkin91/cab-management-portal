package org.example.cab_management_portal.configs.listeners;

import org.springframework.context.event.ContextRefreshedEvent;

public interface ApplicationStartupListeners {

    public void handleContextRefresh(final ContextRefreshedEvent event);
}
