package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.os.Bundle;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.Serializable;

import cl.uchile.ing.adi.quicklooklib.fragments.AbstractFragment;

/**
 * AbstractItem has most of the methods related with items in the library
 * The items
 */
public abstract class AbstractItem {

    public static String ITEM_PATH = "path";
    public static String ITEM_TYPE = "type";


    protected String name;
    protected String type;
    protected long size;
    protected String path;
    protected AbstractFragment fragment;
    private String virtualPath;


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
     * Uses Java Files API and obtains size of file.
     */
    public static long getSizeFromPath(String path) {
        File file = new File(path);
        return file.length();
    }

    /**
     * Helper class. Obtains the name of a file using the path.
     * @param path path of file
     * @return name of file
     */
    public static String getNameFromPath(String path) {
        String[] splitPath = path.split("/");
        return splitPath[splitPath.length-1];
    }

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
            prepareFragment();
        }
        return fragment;
    }

    public void prepareFragment() {
        Bundle b = new Bundle();
        b.putString(ITEM_PATH,this.getPath());
        b.putString(ITEM_TYPE,this.getType());
        fragment.setArguments(b);
        fragment.setItem(this);
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

    public abstract String getFormattedType();

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
        String extension = MimeTypeMap.getFileExtensionFromUrl(path).replace(" ","_");
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        } else {
            type = "unknown";
        }
        return type;
    }

    public String getFormattedSize() {
        //Folder case
        if (size==-1) return "";
        String[] suffixes = {"Bytes","KiB", "MiB", "GiB", "TiB"};
        long countSize = size;
        int i;
        for (i = 0; countSize>1024 && i<suffixes.length; i++) {
                 countSize/=1024;
        }
        return ""+countSize+" "+suffixes[i];
    }

    public String getTitle() {
        return this.getName();
    }

    public String getSubTitle() {
        return this.getFormattedType();
    }

    public String toString() {
        return "Item:"+
                "\nName: "+getName()+
                "\nType: "+getType()+
                "\nPath: "+getPath()+
                "\nSize: "+getFormattedSize()+"\n";
    }

    public void clickAction(AbstractFragment.OnListFragmentInteractionListener f) {
        f.onListFragmentInteraction(this);
    }

    public void setVirtualPath(String virtualPath) {
        this.virtualPath = virtualPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
