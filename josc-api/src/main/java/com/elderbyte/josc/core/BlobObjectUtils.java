package com.elderbyte.josc.core;


public final class BlobObjectUtils {

    /**
     * Returns the filename for the given object name (key)
     * Slashes are interpreted as virtual directory indicators.
     *
     * @param objectName The object name to parse.
     * @return Returns the last part after the last '/', if no '/' is found returns the input string.
     */
    public static String extractVirtualFileName(String objectName){
            String[] parts = objectName.split("/");
            return parts[parts.length-1];
    }

    /**
     * Extracts the extension from a object name (key).
     * Only the file name part is considered for extension scanning.
     *
     * @param objectName The object name
     * @return Returns the extension with the dot, such as '.png'
     */
    public static String extractVirtualExtensionWithDot(String objectName){
        String filename = extractVirtualFileName(objectName);
        int lastPoint = filename.lastIndexOf(".");
        if(lastPoint > 0){
            return filename.substring(lastPoint, filename.length());
        }
        return null;
    }

}
