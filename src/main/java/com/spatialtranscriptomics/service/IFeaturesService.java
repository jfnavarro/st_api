/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */


package com.spatialtranscriptomics.service;

import com.spatialtranscriptomics.model.FeaturesMetadata;
import com.spatialtranscriptomics.model.MongoUserDetails;
import java.io.InputStream;
import java.util.List;

/**
 * Interface for the features service.
 */
public interface IFeaturesService {
 
    public boolean datasetIsGranted(String datasetId, MongoUserDetails user);
    
    public List<FeaturesMetadata> listMetadata();
    
    public FeaturesMetadata getMetadata(String id);
    
    public boolean addUpdate(String id, byte[] gzipfile);
    
    public InputStream find(String id);
    
    public void delete(String id);
}
