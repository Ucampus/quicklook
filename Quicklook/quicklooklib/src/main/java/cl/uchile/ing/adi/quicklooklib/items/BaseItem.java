package cl.uchile.ing.adi.quicklooklib.items;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cl.uchile.ing.adi.quicklooklib.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment;

/**
 * BaseItem has most of the methods related with items in the library.
 */
public abstract class BaseItem {

    public static ArrayList<String> BANNED_NAMES = new ArrayList<>();

    public static String ITEM_PATH = "path";
    public static String ITEM_TYPE = "type";
    public static String ITEM_EXTRA = "extra";
    public static String ITEM_MIME = "mime-type";

    public static String DOWNLOAD_PATH;
    public static String CACHE_PATH;

    public static Context context;


    protected String type;
    protected long size;
    protected String path;
    protected Bundle extra;
    protected QuicklookFragment fragment;
    protected int image;
    protected String formattedName;
    private boolean openable;
    private String mime;

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
        //Formatted name for item
        this.formattedName = getContext().getString(R.string.items_default_formatted_name);

        // check if openable
        this.mime = extra.getString("mime-type");
        if(this.mime==null) this.mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(type);
        if(this.mime == null) this.mime = "text/plain";

        Intent i = new Intent( Intent.ACTION_VIEW );
        i.setDataAndType(Uri.parse(path), mime);
        PackageManager manager = getContext().getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(i, 0);
        this.openable = (infos.size() > 0);
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
     * Prepares the fragment if it's not prepared and returns it
     * @return the Fragment.
     */
    public QuicklookFragment getFragment() {
        prepareFragment();
        return fragment;
    }

    public void prepareFragment() {
        Bundle b = new Bundle();
        b.putString(ITEM_PATH,this.getPath());
        b.putString(ITEM_TYPE, this.getType());
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
    public String getFormattedType() {
        return this.formattedName;
    }

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
     * @param path path of file
     * @return String representing extension of file
     */
    public static String loadType(String path) {
        String extension = getExtension(getNameFromPath(path.replace(" ", "_")));
        if (extension == null) {
            return ItemFactory.DEFAULT_MIMETYPE;
        } else {
            return extension;
        }
    }

    /**
     * Returns extension of a path
     * @param file filepath
     * @return extension
     */
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
     * Sets size value
     * @param size size of item
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

    /**
     * Adds a banned word to banned word list.
     * @param banned banned word
     */
    public static void addBannedWord(String banned) {
        BANNED_NAMES.add(banned);
    }

    /**
     * Checks if a word is a banned word.
     * @param banned word we want to check
     * @return true if the word is a banned word
     */
    public static boolean isBannedWord(String banned) {
        return BANNED_NAMES.contains(banned);
    }

    /**
     * Returns the download path of files.
     * @return download path of files.
     */
    public static String getDownloadPath() {
        return DOWNLOAD_PATH;
    }

    /**
     * Sets the download path.
     * @param dp new download path
     */
    public static void setDownloadPath(String dp) {
        DOWNLOAD_PATH = dp;
    }

    /**
     * Returns the download cache of files.
     * @return cache path of files
     */
    public static String getCachePath() {
        return CACHE_PATH;
    }

    /**
     * Sets the cache path.
     * @param cp new cache path.
     */
    public static void setCachePath(String cp) {
        CACHE_PATH = cp;
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

    public static void setContext(Context c) {
        context = c;
    }

    public static Context getContext() {
        return context;
    }

    public boolean isOpenable() {
        return this.openable;
    }

    /**
     * Returns the path of file.
     * @return the path of file.
     */
    public String getMime() {
        return this.mime;
    }
}
