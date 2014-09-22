///*
// * Copyright (C) 2012 Spatial Transcriptomics AB
// * Read LICENSE for more information about licensing terms
// * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
// */
//package com.spatialtranscriptomics.model;
//
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
///**
// * This class maps the Annotation data model object into a MongoDB Document We
// * use the @Document annotation of Spring Data for the mapping. We also do data
// * validation using Hibernate validator constraints.
// */
//@Document
//public class Annotation implements IAnnotation {
//
//    @Id
//    String id;
//
//    String annotation;
//
//    @Override
//    public String getId() {
//        return id;
//    }
//
//    @Override
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    @Override
//    public String getAnnotation() {
//        return annotation;
//    }
//
//    @Override
//    public void setAnnotation(String anno) {
//        this.annotation = anno;
//    }
//
//}
