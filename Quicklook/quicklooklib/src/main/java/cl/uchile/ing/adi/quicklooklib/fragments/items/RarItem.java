package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.content.Context;

import com.github.junrar.Archive;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Created by dudu on 20-01-2016.
 */
public class RarItem extends VirtualItem {

    public RarItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
        image = R.drawable.compressed;
    }

    @Override
    public ArrayList<AItem> getItemList() {
        ArrayList<AItem> itemList = new ArrayList<>();
        Archive a;
        try {
            a = new Archive(new FileVolumeManager(new File(getPath())));
        } catch (Exception e) {
            e.printStackTrace();
            return itemList;
        }
        if (a!=null) {
            a.getMainHeader().print();
            FileHeader fh = a.nextFileHeader();
            while (fh!=null) {
                String name = fh.getFileNameString().replace('\\','/').trim();
                String path = name;
                String type = loadRarMimeType(fh);
                long size = fh.getFullPackSize();
                AItem newItem = ItemFactory.getInstance().createItem(path, type, name, size);
                itemList.add(newItem);
                fh = a.nextFileHeader();
            }
        }
        return itemList;
    }

    @Override
    public AItem retrieve(Context context) {
        Archive a;
        String filename = getNameFromPath(this.getVirtualPath());
        try {
            FileOutputStream extracted = context.openFileOutput(filename, Context.MODE_PRIVATE);
            a = new Archive(new FileVolumeManager(new File(getPath())));
            if (a != null) {
                a.getMainHeader().print();
                FileHeader fh = a.nextFileHeader();
                while (fh != null) {
                    String fhName = fh.getFileNameString().replace('\\', '/').trim();
                    if (this.getVirtualPath().equals(fhName)) {
                        a.extractFile(fh, extracted);
                        extracted.close();
                        String dirpath = context.getFilesDir() + "/" + filename +"/";
                        String type = loadRarMimeType(fh);
                        long size = this.size;
                        String name = this.name;
                        AItem newFile = ItemFactory.getInstance().createItem(dirpath,type,name,size);
                        return newFile;
                    }
                    fh = a.nextFileHeader();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String loadRarMimeType(FileHeader fh) {
        if (fh.isDirectory()) {
            return ItemFactory.FOLDER_MIMETYPE;
        } else {
            String path = fh.getFileNameString().replace('\\','/');
            return loadMimeType(path);
        }
    }
}
