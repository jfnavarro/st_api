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
     * Updates a chip.
     * @param chip the chip.
     */
    public void update(Chip chip);

    /**
     * Deletes a chip.
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
