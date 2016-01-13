package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.content.Context;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.ZipFragment;

/**
 * Represents a zip file in the filesystem.
 */
public class ZipItem extends FolderItem {

    // Separator, The path here is a combination of the zip path and
    // the inner zip path.
    public static String SEP = "/@/";

    // Extra properties (inner path)
    private String zippath;

    public ZipItem() {
    }

    /**
     * The constructor is sightly different compared to Abstract item. It has an extra property
     * representing the inner path on the zip.
     * @param path path of the zip.
     * @param mimetype mimetype of the zip. It can be changed, creating virtual items.
     * @param zippath path inside the zip.
     */
    public ZipItem(String path, String mimetype, String zippath) {
        super(path, mimetype);
        try {
            this.zippath = zippath;
            getDataFromFile();
        } catch (Exception e) { e.printStackTrace();}
    }

    /**
     * Simmilar to AbstractItem long constructor, but it specifies a path inside the zip.
     * @param path path of the zip.
     * @param mimetype mimetype of the zip. It can be changed, creating virtual items.
     * @param name name of the zip.
     * @param size size of the zip.
     * @param zippath path isidde the zip
     */
    public ZipItem(String path, String mimetype, String name, long size, String zippath) {
        super(path,mimetype,name,size);
        try {
            this.zippath = zippath;
        } catch (Exception e) { e.printStackTrace();}
    }

    /**
     * An overriden getdataFromFile() method, adapted for zips.
     */
    @Override
    protected void getDataFromFile() {
        File file = new File(this.path);
        this.path = file.getAbsolutePath();
        this.name = file.getName();
        this.type = "application/zip";
        this.size = file.length();

    }

    /**
     * Similar to Files method, but it uses Java's Zip API.
     * @return an ArrayList of items.
     */
    @Override
    public ArrayList<AbstractItem> getElements() {
        ArrayList<AbstractItem> elements = new ArrayList<>();
        try {
            ZipFile zipfile = new ZipFile(this.path);
            for (Enumeration<? extends ZipEntry> e = zipfile.entries();
                 e.hasMoreElements();) {
                ZipEntry ze = e.nextElement();
                if (startsWith(ze.getName(),getZipPath())) {
                    String path = ze.getName();
                    String name = getNameFromPath(path);
                    long size = ze.getSize();
                    String type = loadZipMimeType(path);
                    elements.add(ItemFactory.getInstance().createItem(this.path+SEP+path, type, name, size));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return elements;
    }

    @Override
    public AbstractItem create(String path,String mimetype) {
        //The path can be compound (Zip path + inner zip path, separated by SEP).
        String[] newpath = splitZipPath(path);
        return new ZipItem(newpath[0],mimetype,newpath[1]);
    }

    @Override
    public AbstractItem create(String path, String mimetype, String name, long size) {
        //The path can be compound (Zip path + inner zip path, separated by SEP).
        String[] newpath = splitZipPath(path);
        return new ZipItem(newpath[0],mimetype,name,size,newpath[1]);
    }

    /**
     * Returns the inner zip path.
     * @return inner zip path.
     */
    public String getZipPath() {
        return this.zippath;
    }

    @Override
    public int getImage() {
        return R.drawable.compressed;
    }

    @Override
    protected void createFragment() {
        fragment =  new ZipFragment();
    }

    /**
     * Determines if current path has an internal and external route.
     * @param path path of file
     * @return true if path has internal route.
     */
    public boolean pathHasInternalRoute(String path) {
        return (path.split(SEP).length==2);
    }

    /**
     * Loads mimetype of elements inside zip, using their name.
     * @param path Path of the element.
     * @return a string with the mimetype of the file inside the zip.
     */
    public String loadZipMimeType(String path) {
       ZipFile zipfile;
        try {
            zipfile = new ZipFile(this.path);
        } catch (Exception e) {
            return "default";
        }
            ZipEntry ze = zipfile.getEntry(path);
        if (ze.isDirectory()) {
            return "folder";
        }
        String type= null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    /**
     * Returns true if zeName is into zippath.
     * @param zeName name of zipentry
     * @param zippath actual path of zip
     * @return true if zeName is into zippath.
     */
    public static boolean startsWith(String zeName, String zippath) {
        int currentLevel = zippath.split("/").length;
        int zeLevel = zeName.split("/").length;
        int magicNumber = zippath.equals("") ? 0 : 1;
        //Si la entrada parte con zippath y es hijo directo:
        if (zeName.startsWith(zippath) && currentLevel+magicNumber==zeLevel) {
            return true;
        }
        return false;
    }

    /**
     * Splits compound path and assigns the values to the item
     * @param path compound path.
     * @return separated paths.
     */
    public String[] splitZipPath(String path) {
        String newpath = path;
        String zippath = "";
        if (pathHasInternalRoute(path)) {
            newpath = path.split(SEP)[0];
            zippath += path.split(SEP)[1];
        }
        String[] response = {newpath,zippath};
        Log.d("ZipItem", "path a archivo es "+newpath+" y path en zip es "+zippath);
        return response;
    }

    /**
     * Extracts the elements inside the compressed file.
     * @param context The context of the app
     * @return an item.
     */
    public AbstractItem extract(Context context) {
        try {
            ZipFile zf = new ZipFile(this.getPath());
            ZipEntry ze = zf.getEntry(this.getZipPath());
            InputStream zis = zf.getInputStream(ze);
            String filename = getNameFromPath(this.getZipPath());
            FileOutputStream extracted = context.openFileOutput(filename, context.MODE_PRIVATE);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = zis.read(buffer)) > 0) {
                extracted.write(buffer, 0, len);
            }
            extracted.close();
            String path = context.getFilesDir()+"/"+filename;
            return ItemFactory.getInstance().createItem(path, AbstractItem.loadMimeType(path));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
