package com.st.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.st.model.Chip;

/**
 * Interface for the chip service.
 */
@Service
public interface ChipService {

    /**
     * Returns a chip.
     * @param id the chip ID.
     * @return the chip or null if not found.
     */
    public Chip find(String id);

    /**
     * Returns a chip by name.
     * @param name the name.
     * @return the chip or null if not found.
     */
    public Chip findByName(String name);

    /**
     * Returns all chips.
     * @return the list of null if empty.
     */
    public List<Chip> list();

    /**
     * Adds a chip.
     * @param chip the chip.
     * @return the chip with ID assigned or null if no permissions.
     */
    public Chip add(Chip chip);

    /**
     * Updates a chip.
     * @param chip the chip.
     */
    public void update(Chip chip);

    /**
     * Deletes a chip.
     * @param id the chip ID.
     * @return true if the deletion was successful
     */
    public boolean delete(String id);
}
