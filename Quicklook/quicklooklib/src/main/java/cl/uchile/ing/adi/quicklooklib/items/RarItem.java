package cl.uchile.ing.adi.quicklooklib.items;

import android.content.Context;
import android.os.Bundle;

import com.github.junrar.Archive;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Created by dudu on 20-01-2016.
 */
public class RarItem extends VirtualItem {

    public RarItem(String path, String mimetype, long size, Bundle extra) {
        super(path,mimetype,size,extra);
        image = R.drawable.compressed;
        formattedName = getContext().getString(R.string.items_rar_formatted_name);
    }

    @Override
    public ArrayList<BaseItem> getItemList() {
        ArrayList<BaseItem> itemList = new ArrayList<>();
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
                String type = loadRarType(fh);
                long size = fh.getFullPackSize();
                Bundle extra = this.getExtra();
                BaseItem newItem = ItemFactory.getInstance().createItem(path, type, size,extra);
                itemList.add(newItem);
                fh = a.nextFileHeader();
            }
        }
        return itemList;
    }

    @Override
    public String retrieveItem(String path, String dirpath, Context context) {
        String filename = getNameFromPath(path);
        String newPath = dirpath + filename;
        Archive a;
        try {
            FileOutputStream extracted = new FileOutputStream(new File(newPath));
            a = new Archive(new FileVolumeManager(new File(this.getPath())));
            if (a != null) {
                a.getMainHeader().print();
                FileHeader fh = a.nextFileHeader();
                while (fh != null) {
                    String fhName = fh.getFileNameString().replace('\\', '/').trim();
                    if (fhName.equals(path)) {
                        a.extractFile(fh, extracted);
                        extracted.close();
                        return newPath;
                    }
                    fh = a.nextFileHeader();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String loadRarType(FileHeader fh) {
        if (fh.isDirectory()) {
            return ItemFactory.FOLDER_MIMETYPE;
        } else {
            String path = fh.getFileNameString().replace('\\','/');
            return loadType(path);
        }
    }
}
