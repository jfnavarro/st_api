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
// * This class maps the Feature data model object into a MongoDB Document.
// * IMPORTANT NOTE: An instance of this class does not merely represent an
// * individual chip grid location. Rather, it represents a unique gene in an
// * individual chip grid location.
// * <p/>
// * We use the @Document annotation of Spring Data for the mapping. We also do
// * data validation using Hibernate validator constraints.
// */
//@Document
//public class Feature implements IFeature {
//
//    @Id
//    String id;
//
//    String barcode;
//
//    String gene;
//
//    int hits;
//
//    int x;
//
//    int y;
//
//    String annotation;
//
//    @Override
//    public String getId() {
//        return id;
//    }
//
//    // id is set automatically by MongoDB
//    @Override
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    @Override
//    public String getBarcode() {
//        return barcode;
//    }
//
//    @Override
//    public void setBarcode(String barcode) {
//        this.barcode = barcode;
//    }
//
//    @Override
//    public int getHits() {
//        return hits;
//    }
//
//    @Override
//    public void setHits(int hits) {
//        this.hits = hits;
//    }
//
//    @Override
//    public int getX() {
//        return x;
//    }
//
//    @Override
//    public void setX(int x) {
//        this.x = x;
//    }
//
//    @Override
//    public int getY() {
//        return y;
//    }
//
//    @Override
//    public void setY(int y) {
//        this.y = y;
//    }
//
//    @Override
//    public String getGene() {
//        return this.gene;
//    }
//
//    @Override
//    public void setGene(String gene) {
//        this.gene = gene;
//    }
//
//    @Override
//    public String getAnnotation() {
//        return this.annotation;
//    }
//
//    @Override
//    public void setAnnotation(String ann) {
//        this.annotation = ann;
//    }
//
//}
