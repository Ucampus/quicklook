package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.content.Context;
import android.util.Log;
import android.webkit.MimeTypeMap;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Represents a zip file in the filesystem.
 */
public class TarItem extends VirtualItem {

    public TarItem() {
    }

    public TarItem(String path, String mimetype, String virtualPath) {
        super(path, mimetype,virtualPath);
    }

    public TarItem(String path, String mimetype, String name, long size, String virtualPath) {
        super(path,mimetype,name,size,virtualPath);
    }

    @Override
    public ArrayList<AbstractItem> getElements() {
        ArrayList<AbstractItem> elements = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(getPath());
            InputStream bis = new BufferedInputStream(fis);
            //Check if item is .tar or .tar.gz.
            if (getName().endsWith(".gz")) {
                bis = new GzipCompressorInputStream(bis);
            }
            TarArchiveInputStream tais = new TarArchiveInputStream(bis);
            TarArchiveEntry tae;
            while ((tae = (TarArchiveEntry) tais.getNextEntry()) != null)  {
                if (startsWith(tae.getName(), getVirtualPath())) {
                    String path = tae.getName();
                    String name = getNameFromPath(path);
                    long size = tae.getSize();
                    String type = this.loadTarGzMimeType(tae);
                    elements.add(addToList(path,name,type,size));
                }
            }
        } catch (Exception e) { e.printStackTrace();}
        Log.d("getElements: ", elements.toString() );
        return elements;
    }

    public AbstractItem extract(Context context) {
        FileOutputStream extFile;
        BufferedOutputStream extracted;
        int buffersize = 2048;
        try {
            FileInputStream fis = new FileInputStream(getPath());
            InputStream bis = new BufferedInputStream(fis);
            //Check if item is .tar or .tar.gz
            if (getName().endsWith(".gz")) {
                bis = new GzipCompressorInputStream(bis);
            }
            TarArchiveInputStream tais = new TarArchiveInputStream(bis);
            TarArchiveEntry tae;
            while ((tae = (TarArchiveEntry) tais.getNextEntry()) != null) {
                if (tae.getName().equals(getVirtualPath())) {
                    String dirpath = context.getFilesDir() + "/" + this.getName() +"/";
                    File directory = new File(dirpath);
                    if (!(directory.exists())) directory.mkdir();
                    String path = dirpath + tae.getName();
                    extFile = new FileOutputStream(path);
                    extracted = new BufferedOutputStream(extFile,buffersize);
                    byte[] buffer = new byte[buffersize];
                    int len;
                    while ((len = tais.read(buffer)) > 0) {
                        extracted.write(buffer, 0, len);
                    }
                    extracted.close();
                    return ItemFactory.getInstance().createItem(path, AbstractItem.loadMimeType(path));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String loadTarGzMimeType(TarArchiveEntry tar) {
        if (tar.isDirectory()) {
            return "folder";
        }
        String type= null;
        String path = tar.getName();
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        } else {
            return "default";
        }
        return type;
    }

    @Override
    public String getFormattedType() {
        return "Tar Compressed File";
    }

    @Override
    public AbstractItem create(String path,String mimetype) {
        //The path can be compound (Zip path + inner zip path, separated by SEP).
        String[] newpath = splitVirtualPath(path);
        return new TarItem(newpath[0],mimetype,newpath[1]);
    }

    @Override
    public AbstractItem create(String path, String mimetype, String name, long size) {
        //The path can be compound (Zip path + inner zip path, separated by SEP).
        String[] newpath = splitVirtualPath(path);
        return new TarItem(newpath[0],mimetype,name,size,newpath[1]);
    }

    public boolean isFolder() {
        return false;
    }
}
