package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.content.Context;
import android.os.Bundle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Created by dudu on 15-01-2016.
 */
public class ZipItem extends VirtualItem {

    /**
     * Similar to AItem long constructor, but it specifies a path inside the zip.
     * @param path path of the virtual folder.
     * @param mimetype mimetype of the virtual folder. It can be changed, creating virtual items.
     * @param size size of the virtual folder.
     */
    public ZipItem(String path, String mimetype, long size, Bundle extra) {
        super(path,mimetype,size, extra);
        image = R.drawable.compressed;
    }

    /**
     * Loads mimetype of elements inside zip, using their id.
     * @return a string with the mimetype of the file inside the zip.
     */
    public String LoadZipType(ZipEntry ze) {
        if (ze.isDirectory()) {
            return ItemFactory.FOLDER_MIMETYPE;
        } else {
            return loadType(ze.getName());
        }
    }

    /**
     * Extracts the elements inside the compressed file.
     * @param context The context of the app
     * @return Absolute path of extracted file.
     */
    public String retrieveItem(String path, String dirpath, Context context) {
        String filename = getNameFromPath(path);
        String newPath = dirpath + filename;
        try {
            ZipFile zf = new ZipFile(this.getPath());
            ZipEntry ze = zf.getEntry(path);
            InputStream zis = zf.getInputStream(ze);
            FileOutputStream extracted = new FileOutputStream(new File(newPath));
            int len;
            byte[] buffer = new byte[1024];
            while ((len = zis.read(buffer)) > 0) {
                extracted.write(buffer, 0, len);
            }
            extracted.close();
            return newPath;
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

    public ArrayList<AItem> getItemList() {
        ArrayList<AItem> itemList = new ArrayList<>();
        try {
            ZipFile zipfile = new ZipFile(this.path);
            for (Enumeration<? extends ZipEntry> e = zipfile.entries();
                 e.hasMoreElements();) {
                ZipEntry ze = e.nextElement();
                String path = ze.getName();
                long size = ze.getSize();
                String type = this.LoadZipType(ze);
                Bundle extra = this.getExtra();
                AItem newItem = ItemFactory.getInstance().createItem(path, type, size,extra);
                itemList.add(newItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemList;
    }
}
