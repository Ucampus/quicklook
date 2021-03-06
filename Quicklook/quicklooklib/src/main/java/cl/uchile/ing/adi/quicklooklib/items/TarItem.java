package cl.uchile.ing.adi.quicklooklib.items;

import android.content.Context;
import android.os.Bundle;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Represents a zip file in the filesystem.
 */
public class TarItem extends VirtualItem {

    public TarItem(String path, String mimetype, long size, Bundle extra, Context context) {
        super(path,mimetype,size,extra, context);
        image = R.drawable.compressed;
        formattedName = getContext().getString(R.string.items_tar_formatted_name);
    }

    @Override
    public ArrayList<BaseItem> getItemList() {
        ArrayList<BaseItem> itemList = new ArrayList<>();
        ArrayList<String> itemNames = new ArrayList<>();
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
                long size = tae.getSize();
                String type = this.loadTarGzType(tae);
                Bundle extra = this.getExtra();
                if (this.startsWith(path,getVirtualPath())) {
                    BaseItem newItem = ItemFactory.getInstance().createItem(path, type, size, extra, getContext());
                    itemList.add(newItem);
                }
                itemNames.add(path);
            }
            //Allows to go into a folder automatically if it (zip fix)
            if (getVirtualPath().equals("") && itemList.size()==0 && itemNames.size() > 0) {
                setVirtualPath(itemNames.get(0).split("/")[0]);
                return getItemList();
            }
        } catch (Exception e) { e.printStackTrace();}
        return itemList;
    }

    public String retrieveItem(String path, String dirpath, Context context) {
        String filename = getNameFromPath(path);
        String newPath = dirpath + filename;
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
                if (tae.getName().equals(path)) {
                    extFile = new FileOutputStream(newPath);
                    extracted = new BufferedOutputStream(extFile,buffersize);
                    byte[] buffer = new byte[buffersize];
                    int len;
                    while ((len = tais.read(buffer)) > 0) {
                        extracted.write(buffer, 0, len);
                    }
                    extracted.close();
                    return newPath;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String loadTarGzType(TarArchiveEntry tar) {
        if (tar.isDirectory()) {
            return ItemFactory.FOLDER_MIMETYPE;
        } else {
            return loadType(tar.getName());
        }
    }
}
