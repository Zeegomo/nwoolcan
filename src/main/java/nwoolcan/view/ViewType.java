package nwoolcan.view;

/**
 * Types of views of the application.
 * For each type must exist a resource with the same name, lowercased and with ".fxml" at the end
 */
public enum ViewType {

    /**
     * The main view, the one that is always visible and contains all the others.
     */
    MAIN,
    /**
     * The dashboard scene.
     */
    DASHBOARD,
    /**
     * The warehouse view.
     */
    WAREHOUSE,
    /**
     * The main production view.
     */
    PRODUCTION,
    /**
     * The master table view.
     */
    MASTER_TABLE;

    /**
     * Name of the FXML files associated with this type of view.
     * @return The resource name, relative to this package.
     */
    public String getResourceName() {
        return this.name().toLowerCase() + ".fxml";
    }
}
