package cl.uchile.ing.adi.quicklooklib;

import android.os.Bundle;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import cl.uchile.ing.adi.quicklooklib.items.BaseItem;
import cl.uchile.ing.adi.quicklooklib.items.CodeItem;
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
     * @return ItemFactory
     */
    public static ItemFactory getInstance() {
        return ourInstance;
    }

    private ItemFactory() {
        // Here we register the types of files:
        register(FolderItem.class, FOLDER_MIMETYPE, "mime:"+FOLDER_MIMETYPE);
        register(FileItem.class, DEFAULT_MIMETYPE, "mime:"+DEFAULT_MIMETYPE, "mime:text/calendar");
        register(PDFItem.class, "pdf");
        register(ZipItem.class, "zip");
        register(PictureItem.class, "jpeg", "png", "gif", "jpg", "svg", "mime:image");
        register(CodeItem.class, "php", "html", "java", "css", "xml", "js", "py", "json", "c", "h", "rkt", "r", "mime:application/json");
        register(TxtItem.class, "txt", "mime:text");
        register(TarItem.class, "tar", "gz");
        register(RarItem.class, "rar");
        register(WordItem.class, "doc", "docx");
        register(ExcelItem.class, "xls", "xlsx");
        register(PowerpointItem.class, "ppt", "pptx");
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
     */
    public BaseItem createItem(String path, String type, long size, Bundle extra) {
        type = type.toLowerCase();
        Class c = null;
        if (c==null && dictionary.containsKey(type)) c = dictionary.get(type); // por extension
        if( c == null ) {
            String extraMime = extra.getString("mime-type");
            if (extraMime!=null) {
                String mime = "mime:" + extraMime;
                if (dictionary.containsKey(mime)) {
                    c = dictionary.get(mime); //Mime exacto
                } else if (dictionary.containsKey(mime.split("/")[0])) {
                    c = dictionary.get(mime.split("/")[0]); //mime aproximado.
                }
            }
        }
        if(c==null) c = FileItem.class; // por defecto
        try {
            Constructor<?> constructor;
            constructor = c.getConstructor(String.class, String.class, long.class, Bundle.class);
            return (BaseItem) constructor.newInstance(path,type,size,extra);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new FileItem(path, type, size, extra);//Last resort
    }
}
