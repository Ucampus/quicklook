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
    public static final String DEFAULT_MIMETYPE = "";

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
