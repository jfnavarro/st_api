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

    private static StaticContextAccessor instance;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void registerInstance() {
        instance = this;
    }

    /**
     * Returns the a named bean.
     */
    public static <T> T getBean(Class<T> clazz) {
    	//System.out.println("Getting bean");
        T inst = instance.applicationContext.getBean(clazz);
        //System.out.println("Got bean:" + inst);
        return inst;
    }

}
