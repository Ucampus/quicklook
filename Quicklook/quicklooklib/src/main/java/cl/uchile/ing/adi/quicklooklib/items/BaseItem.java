package cl.uchile.ing.adi.quicklooklib.items;

import android.os.Bundle;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment;

/**
 * BaseItem has most of the methods related with items in the library
 * The items
 */
public abstract class BaseItem {

    public static ArrayList<String> BANNED_NAMES = new ArrayList<>();

    public static String ITEM_PATH = "path";
    public static String ITEM_TYPE = "type";
    public static String ITEM_EXTRA = "extra";

    public static String DOWNLOAD_PATH = "";


    protected String type;
    protected long size;
    protected String path;
    protected Bundle extra;
    protected QuicklookFragment fragment;
    private String virtualPath;
    protected int image;

    /**
     * Constructor of the class, metadata is inserted manually.
     * @param path path of file (it should exist)
     * @param type type of file
     * @param size size of file
     * @param extra extras introduced by bundle.
     */
    public BaseItem(String path, String type, long size, Bundle extra) {
        this.path = path;
        this.size = size;
        this.type = type;
        this.extra = extra;
        //Image for item.
        this.image = R.drawable.document;
    }

    /**
     * Helper class. Obtains the id of a file using the path.
     * @param path path of file
     * @return id of file
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
     * Each implementation of BaseItem associates a fragment with itself. This
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
        b.putBundle(ITEM_EXTRA,this.getExtra());
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

    public Bundle getExtra() {
        return this.extra;
    }

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
     * A estas alturas ya sabemos que el item no es carpeta
     * @param path
     * @return
     */
    public static String loadType(String path) {
        String extension = getExtension(getNameFromPath(path.replace(" ", "_")));
        if (extension == null) {
            return ItemFactory.DEFAULT_MIMETYPE;
        } else {
            return extension;
        }
    }

    public static String getExtension(String file) {
        String[] parts = file.split("\\.");
        int len = parts.length;
        if (len == 0) {
            return null;
        }
        return parts[len-1];
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

    public static void addBannedWord(String banned) {
        BANNED_NAMES.add(banned);
    }

    public static boolean isBannedWord(String banned) {
        return BANNED_NAMES.contains(banned);
    }

    public static String getDownloadPath() {
        return DOWNLOAD_PATH;
    }

    public static void setDownloadPath(String dp) {
        DOWNLOAD_PATH = dp;
    }

    /**
     * Copies an item on internal space to download folder.
     * @return Path of item on downloads folder.
     */
    public String copyItem(String mime) {
        try {
            String itemPath = BaseItem.getDownloadPath()+getName();
            File f = new File(itemPath);
            int copied;
            if (!f.exists()) {
                FileOutputStream fos = new FileOutputStream(itemPath);
                copied = IOUtils.copy(new FileInputStream(getPath()), fos);
                fos.close();
                if(copied<=0) {
                    return null;
                }
            }
            return itemPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
