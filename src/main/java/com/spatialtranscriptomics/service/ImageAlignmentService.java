/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.ImageAlignment;

/**
 * Interface for the imagealignment service.
 */
@Service
public interface ImageAlignmentService {

    /**
     * Finds an alignment.
     * @param id the alignment ID.
     * @return the alignment.
     */
    public ImageAlignment find(String id);

    /**
     * Finds an alignment by name.
     * @param name the alignment name.
     * @return the alignment.
     */
    public ImageAlignment findByName(String name);

    /**
     * Finds all alignments of a chip.
     * @param chipId the chip ID.
     * @return the list.
     */
    public List<ImageAlignment> findByChip(String chipId);

    /**
     * Finds all alignments.
     * @return the list.
     */
    public List<ImageAlignment> list();

    /**
     * Adds an alignment.
     * @param imal the alignment.
     * @return the alignment with ID assigned.
     */
    public ImageAlignment add(ImageAlignment imal);

    /**
     * Updates an alignment.
     * @param imal the alignment.
     */
    public void update(ImageAlignment imal);

    /**
     * Deletes an alignment.
     * @param id the alignment ID.
     */
    public void delete(String id);

    /**
     * Deletes all alignment referencing a certain chip.
     * @param chipId the chip ID.
     * @return the list of deleted items.
     */
    public List<ImageAlignment> deleteForChip(String chipId);

    /**
     * Returns true if the current user may delete an alignment.
     * @param id the alignment ID.
     * @return true is granted rights.
     */
    public boolean deleteIsOkForCurrUser(String id);
}
