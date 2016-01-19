package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.content.Context;
import android.os.Bundle;
import android.webkit.MimeTypeMap;

import java.io.File;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment;

/**
 * AItem has most of the methods related with items in the library
 * The items
 */
public abstract class AItem {

    public static String ITEM_PATH = "path";
    public static String ITEM_TYPE = "type";


    protected String name;
    protected String type;
    protected long size;
    protected String path;
    protected QuicklookFragment fragment;
    private String virtualPath;
    protected int image;

    /**
     * Constructor of the class, metadata is inserted manually.
     * @param path path of file (it should exist)
     * @param mimetype mimetype of file
     * @param name name of file
     * @param size size of file
     */
    public AItem(String path, String mimetype, String name, long size) {
        this.path = path;
        this.name = name;
        this.size = size;
        this.type = mimetype;
        //Image for item.
        this.image = R.drawable.document;
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
     * Uses Java Files API and obtains size of file.
     */
    public static long getSizeFromPath(String path) {
        File file = new File(path);
        return file.length();
    }

    /**
     * Each implementation of AItem associates a fragment with itself. This
     * method defines the fragment.
     */
    protected abstract void createFragment();

    /**
     * Prepares the fragment if it's not prepared and returns it
     * @return the Fragment.
     */
    public QuicklookFragment getFragment() {
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
        return getNameFromPath(this.path);
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
     * Each implementation defines a image for its item.
     * @return the resource id of the image.
     */
    public  int getImage() {
        return this.image;
    }

    /**
     * Returns human-readable string with file type.
     * @return String with file type.
     */
    public abstract String getFormattedType();


    /**
     * Returns human-readable expression of file size
     * @return Strinw with file size
     */
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

    /**
     * Static helper method. Gets the mimetype of file using the extension.
     * @param path path of the file.
     * @return String with mimetype of file.
     */
    public static String loadMimeType(String path) {
        File f = new File(path);
        if (f.isDirectory()) {
            return ItemFactory.FOLDER_MIMETYPE;
        }
        String type ;
        String extension = MimeTypeMap.getFileExtensionFromUrl(path.replace(" ","_"));
        if (extension.equals("ql")) {
            return ItemFactory.QUICKLOOK_MIMETYPE;
        }
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        } else {
            type = ItemFactory.DEFAULT_MIMETYPE;
        }
        return type;
    }

    /**
     * For debug purposes
     * @return String
     */
    public String toString() {
        return "Item:"+
                "\nName: "+getName()+
                "\nType: "+getType()+
                "\nPath: "+getPath()+
                "\nSize: "+getFormattedSize()+"\n";
    }

    /**
     * Sets virtual path value
     * @param virtualPath
     */
    public void setVirtualPath(String virtualPath) {
        this.virtualPath = virtualPath;
    }

    /** Sets Name value
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets size value
     * @param size
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Sets the title for activity when item is shown.
     * @return String with title
     */
    public String getTitle() {
        return this.getName();
    }

    /** Sets the subtitle for activity when item is shown.
     * @return String with subtitle
     */
    public String getSubTitle() {
        return this.getFormattedType();
    }

}
