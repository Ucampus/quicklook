package cl.uchile.ing.adi.quicklooklib.items;

import android.content.Context;
import android.os.Bundle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cl.uchile.ing.adi.quicklooklib.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Created by dudu on 15-01-2016.
 */
public class ZipItem extends VirtualItem {

    /**
     * Similar to BaseItem long constructor, but it specifies a path inside the zip.
     * @param path path of the virtual folder.
     * @param mimetype mimetype of the virtual folder. It can be changed, creating virtual items.
     * @param size size of the virtual folder.
     */
    public ZipItem(String path, String mimetype, long size, Bundle extra, Context context) {
        super(path,mimetype,size, extra, context);
        image = R.drawable.compressed;
        formattedName = getContext().getString(R.string.items_zip_formatted_name);

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

    public ArrayList<BaseItem> getItemList() {
        ArrayList<BaseItem> itemList = new ArrayList<>();
        ArrayList<String> itemNames = new ArrayList<>();
        try {
            ZipFile zipfile = new ZipFile(this.path);
            for (Enumeration<? extends ZipEntry> e = zipfile.entries();
                 e.hasMoreElements();) {
                ZipEntry ze = e.nextElement();
                String path = ze.getName();
                long size = ze.getSize();
                String type = this.LoadZipType(ze);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemList;
    }
}
