package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * Assigns mimetypes to fragments.
 */
public class ItemFactory {

    private HashMap<String,Class> dictionary = new HashMap<>();

    private static ItemFactory ourInstance = new ItemFactory();

    /**
     * Returns an instance of the class.
     * @return
     */
    public static ItemFactory getInstance() {
        return ourInstance;
    }

    /**
     * You need to define new file types here, and their related items.
     */
    private ItemFactory() {
        // Here we register the types of files:
        register("folder", FolderItem.class);
        register("default", DefaultItem.class);
        register("application/pdf", PDFItem.class);
        register("application/zip", ZipItem.class);
        register("image/jpeg", PictureItem.class);
        register("image/png", PictureItem.class);
        register("image/gif", PictureItem.class);
        register("text/plain", TxtItem.class);
        register("application/x-tar", TarItem.class);
        register("application/x-gzip", TarItem.class);
        register("application/vnd.openxmlformats-officedocument.wordprocessingml.document", WordItem.class);
        register("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ExcelItem.class);
        register("application/vnd.openxmlformats-officedocument.presentationml.presentation", PowerpointItem.class);
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
     * @param mimetype mimetype of the item.
     * @param name name of the item.
     * @param size size of the item.
     * @return
     */
    public AbstractItem createItem(String path, String mimetype, String name, long size) {
        Class c = DefaultItem.class;
        AbstractItem item = null;
        if (dictionary.containsKey(mimetype)) {
            c = dictionary.get(mimetype);
        }
        try {
            Constructor<?> constructor = c.getConstructor(String.class, String.class, String.class, long.class);
            item = (AbstractItem)constructor.newInstance(path,mimetype,name,size);
            return item;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }
}
