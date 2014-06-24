/*
*Copyright © 2012 Spatial Transcriptomics AB
*Read LICENSE for more information about licensing terms
*Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
* 
*/

package com.spatialtranscriptomics.component;

import org.joda.time.DateTimeZone;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Performs one-time chores required at the application startup.
 */
@Component
public class StartupHousekeeper implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * (Hopefully) invoked once at app startup (more or less).
     * @param event.
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
            DateTimeZone.setDefault(DateTimeZone.UTC);
    }

  
}