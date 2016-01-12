package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.webkit.MimeTypeMap;

import java.io.File;

import cl.uchile.ing.adi.quicklooklib.fragments.AbstractFragment;

/**
 * AbstractItem has most of the methods related with items in the library
 * The items
 */
public abstract class AbstractItem {

    protected String name;
    protected String type;
    protected long size;
    protected String path;
    protected AbstractFragment fragment;

    /**
     * Default constructor. Used with "create" methods.
     */
    public AbstractItem() {
        this("", "", "", 0);
    }

    /**
     * Constructor of the class. Retrieves metadata with file api.
     * @param path path of file (it must exist)
     * @param mimetype mimetype of file.
     */
    public AbstractItem(String path,String mimetype) {
        this.path = path;
        this.type = mimetype;
        this.getDataFromFile();
    }

    /**
     * Constructor of the class, metadata is inserted manually.
     * @param path path of file (it should exist)
     * @param mimetype mimetype of file
     * @param name name of file
     * @param size size of file
     */
    public AbstractItem(String path, String mimetype, String name, long size) {
        this.path = path;
        this.name = name;
        this.size = size;
        this.type = mimetype;
        this.name = getNameFromPath(path);
    }

    /**
     * Uses Java Files API and obtains name and size of file.
     */
    protected void getDataFromFile() {
        File file = new File(this.path);
        name = file.getName();
        size = file.length();
    }

    /**
     * Helper class. Obtains the name of a file using the path.
     * @param path path of file
     * @return name of file
     */
    protected static String getNameFromPath(String path) {
        String[] splitPath = path.split("/");
        return splitPath[splitPath.length-1];
    }

    /**
     * Creates the items. Is used in the Item factory. Uses Files API.
     * @param path path of the file
     * @param mimetype mimetype of the file
     * @return an Item.
     */
    public abstract AbstractItem create(String path, String mimetype);

    /**
     * Creates the items. Used in the item factory. Data inserted manually.
     * You can create virtual elements (As used in Zip files) with this method.
     * @param path path of the file
     * @param mimetype mimetype of the file
     * @param name name of the file
     * @param size size of the file
     * @return an Item
     */
    public abstract AbstractItem create(String path, String mimetype, String name, long size);

    /**
     * Each implementation of AbstractItem associates a fragment with itself. This
     * method defines the fragment.
     */
    protected abstract void createFragment();

    /**
     * Prepares the fragment if it's not prepared and returns it
     * @return the Fragment.
     */
    public AbstractFragment getFragment() {
        if (fragment == null) {
            createFragment();
            fragment.setItem(this);
        }
        return fragment;
    }

    /**
     * Returns the name of file.
     * @return the name of file.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the path of file.
     * @return the path of file.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the size of file.
     * @return the size of file.
     */
    public long getSize() {
        return this.size;
    }

    /**
     * Returns the path of file.
     * @return the path of file.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Evaluates if item is a folder (Virtual or real one).
     * @return true if item is a folder.
     */
    public boolean isFolder() {
        return false;
    }

    /**
     * Each implementation defines a image for its item.
     * @return the resource id of the image.
     */
    public abstract int getImage();

    /**
     * Static helper method. Gets the mimetype of file using the extension.
     * @param path path of the file.
     * @return String with mimetype of file.
     */
    public static String loadMimeType(String path) {
        File f = new File(path);
        if (f.isDirectory()) {
            return "folder";
        }
        String type= null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

}
