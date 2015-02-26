/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package ninja;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.odftoolkit.odfdom.type.DateTime;
import sirius.kernel.nls.NLS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Represents a stored object.
 *
 * @author Andreas Haufler (aha@scireum.de)
 * @since 2013/08
 */
public class StoredObject {
    private File file;

    /**
     * Creates a new StoredObject based on a file.
     *
     * @param file the contents of the object.
     */
    public StoredObject(File file) {
        this.file = file;
    }

    /**
     * Returns the name of the object.
     *
     * @return the name of the object
     */
    public String getName() {
        return file.getName();
    }

    /**
     * Returns the size of the object.
     *
     * @return a string representation of the byte-size of the object
     */
    public String getSize() {
        return NLS.formatSize(file.length());
    }

    /**
     * Returns the last modified date of the object
     *
     * @return a string representation of the last modification date
     */
    public String getLastModified() {
        return NLS.toUserString(Instant.ofEpochMilli(file.lastModified()));
    }

    public String getLastModifiedISO8601() {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(Instant.ofEpochMilli(file.lastModified()));
    }

    /**
     * Returns the MD5 hash of the object
     *
     * @return string MD5 hash
     */
    public String getMD5Hash() {
        try {
            return Files.hash(file, Hashing.md5()).toString();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Deletes the object
     */
    public void delete() {
        file.delete();
        getPropertiesFile().delete();
    }

    /**
     * Returns the underlying file
     *
     * @return the underlying file containing the stored contents
     */
    public File getFile() {
        return file;
    }

    /**
     * Determins if the object exists
     *
     * @return <tt>true</tt> if the object exists, <tt>false</tt> otherwise
     */
    public boolean exists() {
        return file.exists();
    }

    /**
     * Returns all properties stored along with the object.
     * <p>
     * This is the Content-MD5, Content-Type and any x-amz-meta- header.
     * </p>
     *
     * @return a set of name value pairs representing all properties stored for this object
     * @throws Exception in case of an IO error
     */
    public Set<Map.Entry<Object, Object>> getProperties() throws Exception {
        Properties props = new Properties();
        FileInputStream in = new FileInputStream(getPropertiesFile());
        try {
            props.load(in);
        } finally {
            in.close();
        }

        return props.entrySet();
    }

    /**
     * Returns the file used to store the properties and meta headers.
     *
     * @return the underlying file used to store the meta infos
     */
    public File getPropertiesFile() {
        return new File(file.getParentFile(), "__ninja_" + file.getName() + ".properties");
    }

    /**
     * Stores the given meta infos for the stored object.
     *
     * @param properties properties to store
     * @throws IOException in case of an IO error
     */
    public void storeProperties(Map<String, String> properties) throws IOException {
        Properties props = new Properties();
        props.putAll(properties);
        FileOutputStream out = new FileOutputStream(getPropertiesFile());
        try {
            props.store(out, "");
        } finally {
            out.close();
        }
    }
}
