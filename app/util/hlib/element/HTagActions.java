package util.hlib.element;

public enum HTagActions {
    /**
     * Update existing ID with content. default
     */
    replaceContent, 
    /**
     * Replace existing ID with content
     */
    replace, 
    /**
     * Add content inside existing ID
     */
    addContent,
    /**
     * Delete existing ID
     */
    delete, 
    /**
     * Delete content of existing ID
     */
    deleteContent,
    createBefore, 
    createAfter;
}
