/*
*Copyright © 2012 Spatial Transcriptomics AB
*Read LICENSE for more information about licensing terms
*Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
* 
*/

package com.spatialtranscriptomics.component;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Enables static access of application single-instance beans.
 */
@Component
public class StaticContextAccessor {

    /** This class. **/
    private static StaticContextAccessor instance;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void registerInstance() {
        instance = this;
    }

    /**
     * Returns a bean by name.
     * @param <T> the bean class.
     * @return the bean instance.
     */
    public static <T> T getBean(Class<T> clazz) {
    	
        T inst = instance.applicationContext.getBean(clazz);
        
        return inst;
    }

}
