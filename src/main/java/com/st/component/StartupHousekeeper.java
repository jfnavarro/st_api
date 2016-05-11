package com.st.component;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Performs one-time chores required at the application startup.
 */
@Component
public class StartupHousekeeper implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = Logger.getLogger(StartupHousekeeper.class);
    
    /**
     * (Hopefully) invoked once at app startup (more or less).
     *
     * @param event.
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Set the default timezone to UTC.
        logger.info("Setting the default time zone to UTC.");
        DateTimeZone.setDefault(DateTimeZone.UTC);
    }

}
