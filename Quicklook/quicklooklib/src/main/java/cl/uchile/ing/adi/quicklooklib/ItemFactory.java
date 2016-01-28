package cl.uchile.ing.adi.quicklooklib;

import android.os.Bundle;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import cl.uchile.ing.adi.quicklooklib.items.BaseItem;
import cl.uchile.ing.adi.quicklooklib.items.ExcelItem;
import cl.uchile.ing.adi.quicklooklib.items.FileItem;
import cl.uchile.ing.adi.quicklooklib.items.FolderItem;
import cl.uchile.ing.adi.quicklooklib.items.PDFItem;
import cl.uchile.ing.adi.quicklooklib.items.PictureItem;
import cl.uchile.ing.adi.quicklooklib.items.PowerpointItem;
import cl.uchile.ing.adi.quicklooklib.items.RarItem;
import cl.uchile.ing.adi.quicklooklib.items.TarItem;
import cl.uchile.ing.adi.quicklooklib.items.TxtItem;
import cl.uchile.ing.adi.quicklooklib.items.WordItem;
import cl.uchile.ing.adi.quicklooklib.items.ZipItem;

/**
 * Assigns mimetypes to fragments.
 */
public class ItemFactory {

    private HashMap<String,Class> dictionary = new HashMap<>();

    private static ItemFactory ourInstance = new ItemFactory();

    public static final String FOLDER_MIMETYPE = "application/folder";
    public static final String DEFAULT_MIMETYPE = "";

    /**
     * Returns an instance of the class.
     * @return
     */
    public static ItemFactory getInstance() {
        return ourInstance;
    }

    private ItemFactory() {
        // Here we register the types of files:
        register(FolderItem.class, FOLDER_MIMETYPE);
        register(FileItem.class, DEFAULT_MIMETYPE);
        register(PDFItem.class, "pdf");
        register(ZipItem.class, "zip");
        register(PictureItem.class, "jpeg", "png", "gif", "jpg");
        register(TxtItem.class, "txt","php","html");
        register(TarItem.class, "tar", "gz");
        register(RarItem.class, "rar");
        register(WordItem.class, "doc", "docx");
        register(ExcelItem.class, "xls","xlsx");
        register(PowerpointItem.class, "ppt","pptx");
    }

    /**
     * Adds more than one extension to the factory with the same className.
     * @param className className associated.
     * @param extensions extensions.
     */
    public void register(Class className,String... extensions) {
        for (String extension:extensions) {
            dictionary.put(extension, className);
        }
    }

    /**
     * Creates item without using the Files API. It can create virtual items.
     * @param path path of the item.
     * @param type mimetype of the item.
     * @param size size of the item.
     * @return
     */
    public BaseItem createItem(String path, String type, long size, Bundle extra) {
        type = type.toLowerCase();
        Class c = FileItem.class;
        BaseItem item = null;
        if (dictionary.containsKey(type)) {
            c = dictionary.get(type);
        }
        try {
            Constructor<?> constructor = c.getConstructor(String.class, String.class, long.class, Bundle.class);
            item = (BaseItem)constructor.newInstance(path,type,size,extra);
            return item;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }
}
