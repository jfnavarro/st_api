/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Chip;

/**
 * Interface for the chip service.
 */
@Service
public interface ChipService {

    /**
     * Returns a chip.
     * @param id the chip ID.
     * @return the chip.
     */
    public Chip find(String id);

    /**
     * Returns a chip by name.
     * @param name the name.
     * @return the chip.
     */
    public Chip findByName(String name);

    /**
     * Returns all chips.
     * @return the list.
     */
    public List<Chip> list();

    /**
     * Adds a chip.
     * @param chip the chip.
     * @return the chip with ID assigned.
     */
    public Chip add(Chip chip);

    /**
     * Adds a chip's file to S3.
     * @param id the ID of the chip.
     * @param chipFile the original file of the chip
     */
    public void addFileToS3(String id, byte[] chipFile);
    
    /**
     * Updates a chip.
     * @param chip the chip.
     */
    public void update(Chip chip);

    /**
     * Deletes a chip and its file from S3.
     * @param id the chip ID.
     */
    public void delete(String id);

    /**
     * Returns true if the current user may delete a chip.
     * @param id the chip ID.
     * @return true if granted delete rights.
     */
    public boolean deleteIsOkForCurrUser(String id);
}
