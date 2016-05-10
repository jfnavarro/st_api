package com.st.model;

/**
 * This class implements the VersionSupportInfo object. VersionSupportInfo is
 * not mapped to a MongoDB document. Instead, it is being instantiated in
 * VersionSupportInfoController.class.
 * 
 * The version support holds the min required client version. 
 */
public class VersionSupportInfo implements IVersionSupportInfo {

    String minSupportedClientVersion;

     /**
     * Default constructor is needed by Jackson, in
     * case other constructors are added.
     */
    public VersionSupportInfo() {}
    
    
    @Override
    public String getMinSupportedClientVersion() {
        return this.minSupportedClientVersion;
    }

    
    @Override
    public void setMinSupportedClientVersion(String version) {
        this.minSupportedClientVersion = version;
    }

}
