package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.VirtualFragment;

/**
 * Represents a Virtual file in the filesystem, like a Zip.
 */
public abstract class VirtualItem extends ListItem {

    // Separator, The path here is a combination of the zip path and
    // the inner zip path.
    public static String SEP = "/@/";

    // Extra properties (inner path)
    protected String virtualPath;

    public VirtualItem() {
    }

    /**
     * The constructor is sightly different compared to Abstract item. It has an extra property
     * representing the inner path on the virtual element.
     * @param path path of the zip.
     * @param mimetype mimetype of the zip. It can be changed, creating virtual items.
     * @param virtualPath path inside the zip.
     */
    public VirtualItem(String path, String mimetype, String virtualPath) {
        super(path, mimetype);
        try {
            this.virtualPath = virtualPath;
            getDataFromFile();
        } catch (Exception e) { e.printStackTrace();}
    }

    /**
     * Simmilar to AbstractItem long constructor, but it specifies a path inside the zip.
     * @param path path of the virtual folder.
     * @param mimetype mimetype of the virtual folder. It can be changed, creating virtual items.
     * @param name name of the virtual folder.
     * @param size size of the virtual folder.
     * @param virtualPath path inside the virtual folder.
     */
    public VirtualItem(String path, String mimetype, String name, long size, String virtualPath) {
        super(path,mimetype,name,size);
        this.virtualPath = virtualPath;
    }

    /**
     * An overriden getdataFromFile() method, adapted for virtual folders.
     */
    @Override
    protected void getDataFromFile() {
        File file = new File(this.path);
        this.path = file.getAbsolutePath();
        this.name = file.getName();
        this.type = loadMimeType(this.path);
        this.size = file.length();

    }


    /**
     * Gets elements inside a VirtualItem Folder.
     * @return An arraylist with the virtual elements of folder.
     */
    public abstract ArrayList<AbstractItem> getElements();
    public abstract AbstractItem extract(Context context);

    /**
     * Returns the inner zip path.
     * @return inner zip path.
     */
    public String getVirtualPath() {
        return this.virtualPath;
    }

    @Override
    public int getImage() {
        return R.drawable.compressed;
    }

    @Override
    protected void createFragment() {
        fragment =  new VirtualFragment();
    }

    @Override
    public void prepareFragment() {
        Bundle b = new Bundle();
        String innerRoute = this.getVirtualPath().equals("") ? "" : SEP+this.getVirtualPath();
        b.putString(ITEM_PATH,this.getPath()+innerRoute);
        b.putString(ITEM_TYPE,this.getType());
        fragment.setArguments(b);
        fragment.setItem(this);
    }

    /**
     * Determines if current path has an internal and external route.
     * @param path path of file
     * @return true if path has internal route.
     */
    public static boolean pathHasInternalRoute(String path) {
        return (path.split(SEP).length>=2);
    }

    /**
     * Returns true if zeName is into virtualPath.
     * @param zeName name of zipentry
     * @param zippath actual path of zip
     * @return true if zeName is into virtualPath.
     */
    public static boolean startsWith(String zeName, String zippath) {
        int currentLevel = zippath.split("/").length;
        int zeLevel = zeName.split("/").length;
        int magicNumber = zippath.equals("") ? 0 : 1;
        //Si la entrada parte con virtualPath y es hijo directo:
        if (zeName.startsWith(zippath) && currentLevel+magicNumber==zeLevel) {
            return true;
        }
        return false;
    }

    /**
     * Splits compound path and assigns the values to the item
     * @param path compound path.
     * @return separated paths.
     */
    public static String[] splitVirtualPath(String path) {
        String newpath = path;
        String zippath = "";
        if (pathHasInternalRoute(path)) {
            int mid = path.lastIndexOf(SEP);
            int sepLen = SEP.length();
            newpath = path.substring(0,mid);
            zippath += path.substring(mid+sepLen);
        }
        String[] response = {newpath,zippath};
        Log.d("VirtualItem", "path a archivo es "+newpath+" y path virtual es \""+zippath+"\"");
        return response;
    }

    public String getTitle() {
        return this.getNameFromPath(getPath())+"/"+getNameFromPath(getVirtualPath());
    }

    public String getSubTitle() {
        return this.getFormattedType();
    }

    @Override
    public String getFormattedType() {
        return "Virtual File";
    }

    public String toString() {
        return super.toString()+
                "Virtual Path: "+this.getVirtualPath()+"\n";
    }

    @Override
    public AbstractItem create(String path,String mimetype) {
        //The path can be compound (Zip path + inner zip path, separated by SEP).
        String[] newpath = splitVirtualPath(path);
        return new ZipItem(newpath[0],mimetype,newpath[1]);
    }

    @Override
    public AbstractItem create(String path, String mimetype, String name, long size) {
        //The path can be compound (Zip path + inner zip path, separated by SEP).
        String[] newpath = splitVirtualPath(path);
        return new ZipItem(newpath[0],mimetype,name,size,newpath[1]);
    }

    public AbstractItem addToList(String path, String name, String type, long size) {
        AbstractItem preItem = ItemFactory.getInstance().createItem(this.path + SEP + path, type, name, size);
        String anotherSep = preItem instanceof VirtualItem ? SEP : "";
        return ItemFactory.getInstance().createItem(this.path + SEP + path + anotherSep, type, name, size);
    }
}
