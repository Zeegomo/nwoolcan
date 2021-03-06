package nwoolcan.controller.file;

import nwoolcan.model.brewery.Brewery;
import nwoolcan.utils.Empty;
import nwoolcan.utils.Result;

import java.io.File;
import java.io.InputStream;

/**
 * Controller to handle files.
 */
public interface FileController {
    /**
     * Saves the current state of the Brewery to a file with the given name.
     * @param filename the name of the file to be saved.
     * @param toSave the object to save.
     * @return a {@link Result} describing the operation's outcome.
     */
    Result<Empty> saveBreweryTo(File filename, Brewery toSave);
    /**
     * Loads the state of the Brewery from the file with the given name.
     * @param filename the name of the file to load.
     * @return a {@link Result} describing the operation's outcome.
     */
    Result<Brewery> loadBreweryFrom(File filename);
    /**
     * Loads the state of the Brewery from the stream.
     * @param stream the stream to load.
     * @return a {@link Result} describing the operation's outcome.
     */
    Result<Brewery> loadBreweryFromJar(InputStream stream);
    /**
     * Checks if the given filename is valid.
     * @param filename the filename to check.
     * @return if the given filename is valid.
     */
    boolean checkFilename(File filename);
    /**
     * Returns the valid file extension.
     * @return the valid file extension.
     */
    String getValidExtension();
}
