package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.content.Context;
import android.webkit.MimeTypeMap;

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
     * Simmilar to AItem long constructor, but it specifies a path inside the zip.
     * @param path path of the virtual folder.
     * @param mimetype mimetype of the virtual folder. It can be changed, creating virtual items.
     * @param name name of the virtual folder.
     * @param size size of the virtual folder.
     */
    public ZipItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
        image = R.drawable.compressed;
    }

    /**
     * Loads mimetype of elements inside zip, using their name.
     * @return a string with the mimetype of the file inside the zip.
     */
    public String LoadZipMimeType(ZipEntry ze) {
        if (ze.isDirectory()) {
            return ItemFactory.FOLDER_MIMETYPE;
        }
        String type;
        String path= ze.getName();
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        } else {
            return ItemFactory.DEFAULT_MIMETYPE;
        }
        return type;
    }

    /**
     * Extracts the elements inside the compressed file.
     * @param context The context of the app
     * @return an item.
     */
    public AItem retrieve(Context context) {
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
            String type = loadMimeType(path);
            long size = AItem.getSizeFromPath(path);
            String name = AItem.getNameFromPath(path);
            return ItemFactory.getInstance().createItem(path, type,name,size);
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
                String name = getNameFromPath(path);
                long size = ze.getSize();
                String type = this.LoadZipMimeType(ze);
                AItem newItem = createForList(path, type, name, size);
                itemList.add(newItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemList;
    }
}
