package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.content.Context;

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

import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Represents a zip file in the filesystem.
 */
public class TarItem extends VirtualItem {

    public TarItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
        image = R.drawable.compressed;
    }

    @Override
    public ArrayList<AItem> getItemList() {
        ArrayList<AItem>itemList = new ArrayList<>();
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
                String path = tae.getName();
                String name = getNameFromPath(path);
                long size = tae.getSize();
                String type = this.loadTarGzMimeType(tae);
                AItem newItem = ItemFactory.getInstance().createItem(path, type, name, size);
                itemList.add(newItem);
            }
        } catch (Exception e) { e.printStackTrace();}
        return itemList;
    }

    public AItem retrieve(Context context) {
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
                    String type =  AItem.loadMimeType(path);
                    long size = AItem.getSizeFromPath(path);
                    String name = AItem.getNameFromPath(path);
                    return ItemFactory.getInstance().createItem(path,type,name,size);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String loadTarGzMimeType(TarArchiveEntry tar) {
        if (tar.isDirectory()) {
            return ItemFactory.FOLDER_MIMETYPE;
        } else {
            return loadMimeType(tar.getName());
        }
    }

    @Override
    public String getFormattedType() {
        return "Tar Compressed File";
    }
}
