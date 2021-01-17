package org.example.cab_management_portal.configs.managers;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.configs.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationManager {

    @Autowired
    ApplicationContext applicationContext;


}
