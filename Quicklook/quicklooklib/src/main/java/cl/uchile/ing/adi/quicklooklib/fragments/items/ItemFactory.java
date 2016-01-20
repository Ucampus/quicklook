package cl.uchile.ing.adi.quicklooklib.fragments.items;

import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * Assigns mimetypes to fragments.
 */
public class ItemFactory {

    public static final String QUICKLOOK_MIMETYPE = "text/quicklook-virtual-folder";
    private HashMap<String,Class> dictionary = new HashMap<>();

    private static ItemFactory ourInstance = new ItemFactory();

    public static final String FOLDER_MIMETYPE = "application/folder";
    public static final String DEFAULT_MIMETYPE = "application/default";

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
        register(FOLDER_MIMETYPE, FolderItem.class);
        register(DEFAULT_MIMETYPE, FileItem.class);
        register("application/pdf", PDFItem.class);
        register("application/zip", ZipItem.class);
        register("image/jpeg", PictureItem.class);
        register("image/png", PictureItem.class);
        register("image/gif", PictureItem.class);
        register("text/plain", TxtItem.class);
        register("application/x-tar", TarItem.class);
        register("application/x-gzip", TarItem.class);
        register("application/x-rar-compressed", RarItem.class);
        register("application/rar", RarItem.class);
        register("application/vnd.openxmlformats-officedocument.wordprocessingml.document", WordItem.class);
        register("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ExcelItem.class);
        register("application/vnd.openxmlformats-officedocument.presentationml.presentation", PowerpointItem.class);
        register(QUICKLOOK_MIMETYPE, JsonItem.class);
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
     * @param id id of the item.
     * @param size size of the item.
     * @return
     */
    public AItem createItem(String path, String mimetype, String id, long size) {
        Class c = FileItem.class;
        AItem item = null;
        if (dictionary.containsKey(mimetype)) {
            c = dictionary.get(mimetype);
        }
        try {
            Constructor<?> constructor = c.getConstructor(String.class, String.class, String.class, long.class);
            item = (AItem)constructor.newInstance(path,mimetype,id,size);
            return item;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }
}
