package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.QuicklookActivity;
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
     * @param size size of the virtual folder.
     */
    public VirtualItem(String path, String mimetype, long size, Bundle extra) {
        super(splitVirtualPath(path)[0], mimetype, size, extra);
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
            if (startsWith(elem.getPath(), this.getVirtualPath()) &&
                    !isBannedWord(elem.getName())) {
                approvedElements.add(createForList(elem));
            }
        }
        //Allows to go into a folder automatically if it (zip fix)
        if (getVirtualPath().equals("") && approvedElements.size()==0 && itemList.size() >0) {
            setVirtualPath(itemList.get(0).getPath().split("/")[0]);
            return getElements();
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

    public void setVirtualPath(String s) {
        this.virtualPath = s;
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
     * @param zeName id of zipentry
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
        VirtualItem parentItem = (VirtualItem)mListener.getFragment().getItem();
        if (item instanceof FolderItem) {
            String path = item.getPath();
            long size = item.getSize();
            String type = parentItem.getType();
            VirtualItem newItem = (VirtualItem)ItemFactory.getInstance().createItem(path, type, size);
            mListener.onListFragmentInteraction(newItem);
        } else {
            mListener.onListFragmentRetrieval(item,parentItem);
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
     * @param preItem Item not prepared for this (?)
     * @return item
     */
    public AItem createForList(AItem preItem) {
        String newpath = this.path + SEP + preItem.path
                        + (preItem instanceof VirtualItem ? VirtualItem.SEP : "");
        return ItemFactory.getInstance().createItem(newpath, preItem.type, preItem.size);
    }
    /**
     * Gets an specific item from the virtual object and copies it to main memory.
     * @param toRetrieve item inside this
     * @param context Current application context
     * @return Abstract item with object
     */
    public AItem retrieve(AItem toRetrieve, Context context) {
        String innerPath = context.getFilesDir().getAbsolutePath()+"/quicklook/";
        File folder = new File(innerPath);
        if (!folder.exists()) folder.mkdirs();
        String path = retrieveItem(splitVirtualPath(toRetrieve.path)[1],innerPath,context);
        String type = toRetrieve.getType();
        long size = toRetrieve.getSize();
        return ItemFactory.getInstance().createItem(path,type,size);
    }

    /**
     * Allows to retrieve an item from a virtual item, using its id.
     * @param id Internal identifier of file
     * @param dirpath Output path of the retrieved file
     * @param context Context of application
     * @return String with complete path of file.
     */
    public abstract String retrieveItem(String id, String dirpath, Context context);


    public String getTitle() {
        String name = this.getNameFromPath(getVirtualPath());
        if (name.equals("")) {
            name = this.getNameFromPath(getPath());
        }
        return name;
    }

    @Override
    public String getSubTitle() {
        return this.getVirtualPath();
    }



}
