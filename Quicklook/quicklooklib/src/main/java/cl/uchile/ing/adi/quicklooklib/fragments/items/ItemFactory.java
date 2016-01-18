package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.util.Log;

import java.util.HashMap;

/**
 * Assigns mimetypes to fragments.
 */
public class ItemFactory {

    private HashMap<String,AbstractItem> dictionary = new HashMap<>();

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
        register("folder", new FolderItem());
        register("default",new DefaultItem());
        register("application/pdf", new PdfItem());
        register("application/zip", new ZipItem());
        register("image/jpeg", new PictureItem());
        register("image/png", new PictureItem());
        register("image/gif", new PictureItem());
        register("text/plain", new TxtItem());
        register("application/x-tar", new TarItem());
        register("application/x-gzip", new TarItem());
        register("application/vnd.openxmlformats-officedocument.wordprocessingml.document", new WordItem());
        register("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new ExcelItem());
        register("application/vnd.openxmlformats-officedocument.presentationml.presentation", new PowerpointItem());
    }

    /**
     * Adds new types of files
     * @param mime mimetype.
     * @param item item associated.
     */
    public void register(String mime, AbstractItem item) {
        dictionary.put(mime, item);
    }

    /**
     * Creates an item using Files API. The file must exist.
     * @param path path of the file
     * @param mimetype mimetype
     * @return an Item
     */
    public AbstractItem createItem(String path, String mimetype) {
        if (dictionary.containsKey(mimetype)) {
            return dictionary.get(mimetype).create(path,mimetype);
        } else {
            return dictionary.get("default").create(path,mimetype);
        }
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
        if (dictionary.containsKey(mimetype)) {
            return dictionary.get(mimetype).create(path,mimetype,name,size);
        } else {
            Log.d("ItemFactory", "No logramos encontrar ese tipo de item, cayendo en default.");
            return dictionary.get("default").create(path,mimetype,name,size);
        }
    }

}
