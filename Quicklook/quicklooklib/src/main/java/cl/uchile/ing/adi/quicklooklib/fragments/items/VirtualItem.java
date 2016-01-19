package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.ListFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.adapters.VirtualRecyclerViewAdapter;

/**
 * Represents a Virtual file in the filesystem, like a Zip.
 */
public abstract class VirtualItem extends AItem implements ListItem {

    // Separator, The path here is a combination of the zip path and
    // the inner zip path.
    public static String SEP = "/@/";

    // Extra properties (inner path)
    protected String virtualPath;

    protected ArrayList<AItem> itemList;

    /**
     * Simmilar to AItem long constructor, but it specifies a path inside the zip.
     * @param path path of the virtual folder.
     * @param mimetype mimetype of the virtual folder. It can be changed, creating virtual items.
     * @param name name of the virtual folder.
     * @param size size of the virtual folder.
     */
    public VirtualItem(String path, String mimetype, String name, long size) {
        super(splitVirtualPath(path)[0], mimetype, name, size);
        this.virtualPath = splitVirtualPath(path)[1];
    }

    /**
     * Gets elements inside a VirtualItem Folder.
     * @return An arraylist with the virtual elements of folder.
     */
    @Override
    public ArrayList<AItem> getElements() {
        if (itemList==null) {
            itemList = getItemList();
        }
        ArrayList<AItem> approvedElements = new ArrayList<>();
        for (AItem elem:itemList) {
            if (startsWith(splitVirtualPath(elem.getPath())[1], this.getVirtualPath())) {
                approvedElements.add(elem);
            }
        }
        Log.d("getElements: ", "El filtro es " + getVirtualPath());
        Log.d("getElements: ", approvedElements.toString());
        return approvedElements;
    }

    /**
     * Returns the inner zip path.
     * @return inner zip path.
     */
    public String getVirtualPath() {
        return this.virtualPath;
    }

    @Override
    protected void createFragment() {
        fragment =  new ListFragment();
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


    /**
     * Defines an action to do when a virtual item is clicked. The action could be enter to a folder,
     * or retrieve a item from the folder.
     * @param mListener
     * @param item
     */
    public static void onClick(QuicklookFragment.OnListFragmentInteractionListener mListener, AItem item) {
        String name = item.getName();
        String path = item.getPath();
        long size = item.getSize();
        String type = mListener.getFragment().getItem().getType();
        VirtualItem newItem = (VirtualItem)ItemFactory.getInstance().createItem(path, type, name, size);
        if (item instanceof FolderItem) {
            mListener.onListFragmentInteraction(newItem);
        } else {
            mListener.onListFragmentRetrieval(newItem);
        }
    }

    /**
     * Preapares the item list with all the items inside the virtual object
     * @return ArrayList with all the items inside virtual object.
     */
    public abstract ArrayList<AItem> getItemList();

    public RecyclerView.Adapter getAdapter(QuicklookFragment.OnListFragmentInteractionListener mListener) {
        return new VirtualRecyclerViewAdapter(this.getElements(), mListener);
    }

    /**
     * Creates an item for the list of items.
     * @param path Path of the item
     * @param type Type of the item
     * @param name Name of the item
     * @param size Size of the item
     * @return item
     */
    public AItem createForList(String path, String type, String name, long size) {
        String newpath = this.path + SEP + path;
        AItem preItem = ItemFactory.getInstance().createItem(newpath, type, name, size);
        String anotherSep = preItem instanceof VirtualItem ? VirtualItem.SEP : "";
        return ItemFactory.getInstance().createItem(newpath + anotherSep, type, name, size);
    }
    /**
     * Gets an specific item from the virtual object and copies it to main memory.
     * @param context Current application context
     * @return Abstract item with object
     */
    public abstract AItem retrieve(Context context);




}
