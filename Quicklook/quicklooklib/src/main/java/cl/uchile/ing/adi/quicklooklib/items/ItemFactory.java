package cl.uchile.ing.adi.quicklooklib.items;

import android.os.Bundle;

import java.lang.reflect.Constructor;
import java.util.HashMap;

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
        register(FOLDER_MIMETYPE, FolderItem.class);
        register(DEFAULT_MIMETYPE, FileItem.class);
        register("pdf", PDFItem.class);
        register("zip", ZipItem.class);
        register("jpeg", PictureItem.class);
        register("jpg", PictureItem.class);
        register("png", PictureItem.class);
        register("gif", PictureItem.class);
        register("txt", TxtItem.class);
        register("tar", TarItem.class);
        register("gz", TarItem.class);
        register("rar", RarItem.class);
        register("docx", WordItem.class);
        register("xlsx", ExcelItem.class);
        register("pptx", PowerpointItem.class);
    }

    /**
     * Adds new types of files
     * @param mime mimetype.
     * @param item item associated.
     */
    public void register(String mime, Class item) {
        dictionary.put(mime, item);
    }

    /**
     * Creates item without using the Files API. It can create virtual items.
     * @param path path of the item.
     * @param type mimetype of the item.
     * @param size size of the item.
     * @return
     */
    public BaseItem createItem(String path, String type, long size, Bundle extra) {
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
