package com.spatialtranscriptomics.util;

/**
 * Convenience methods for Strings.
 */
public class StringOperations {
 
    /**
     * Returns a string representation of a byte count.
     * @param bytes the no of bytes.
     * @return the readable string.
     */
    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) { return bytes + " B"; }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp-1) + ("i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    
}
