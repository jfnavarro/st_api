package com.spatialtranscriptomics.component;

import org.joda.time.DateTimeZone;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartupHousekeeper implements ApplicationListener<ContextRefreshedEvent> {

	public void onApplicationEvent(ContextRefreshedEvent event) {
		DateTimeZone.setDefault(DateTimeZone.UTC);
	}

  
}