package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.content.Context;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by dudu on 15-01-2016.
 */
public class ZipItem extends VirtualItem {

    public ZipItem() {
        super();
    }


    /**
     * The constructor is sightly different compared to Abstract item. It has an extra property
     * representing the inner path on the virtual element.
     * @param path path of the zip.
     * @param mimetype mimetype of the zip. It can be changed, creating virtual items.
     * @param virtualPath path inside the zip.
     */
    public ZipItem(String path, String mimetype, String virtualPath) {
        super(path, mimetype,virtualPath);
    }

    /**
     * Simmilar to AbstractItem long constructor, but it specifies a path inside the zip.
     * @param path path of the virtual folder.
     * @param mimetype mimetype of the virtual folder. It can be changed, creating virtual items.
     * @param name name of the virtual folder.
     * @param size size of the virtual folder.
     * @param virtualPath path inside the virtual folder.
     */
    public ZipItem(String path, String mimetype, String name, long size, String virtualPath) {
        super(path,mimetype,name,size,virtualPath);
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
                if (startsWith(ze.getName(), getVirtualPath())) {
                    String path = ze.getName();
                    String name = getNameFromPath(path);
                    long size = ze.getSize();
                    String type = this.LoadZipMimeType(path);
                    elements.add(addToList(path,name,type,size));
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
        String[] newpath = splitVirtualPath(path);
        return new ZipItem(newpath[0],mimetype,newpath[1]);
    }

    @Override
    public AbstractItem create(String path, String mimetype, String name, long size) {
        //The path can be compound (Zip path + inner zip path, separated by SEP).
        String[] newpath = splitVirtualPath(path);
        return new ZipItem(newpath[0],mimetype,name,size,newpath[1]);
    }

    /**
     * Loads mimetype of elements inside zip, using their name.
     * @return a string with the mimetype of the file inside the zip.
     */
    public String LoadZipMimeType(String path) {
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
        } else {
            return "default";
        }
        return type;
    }

    /**
     * Extracts the elements inside the compressed file.
     * @param context The context of the app
     * @return an item.
     */
    public AbstractItem extract(Context context) {
        try {
            ZipFile zf = new ZipFile(this.getPath());
            ZipEntry ze = zf.getEntry(this.getVirtualPath());
            InputStream zis = zf.getInputStream(ze);
            String filename = getNameFromPath(this.getVirtualPath());
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

    @Override
    public String getFormattedType() {
        return "Zip Compressed File";
    }

    public boolean isFolder() {
        return false;
    }
}
