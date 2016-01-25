package cl.uchile.ing.adi.quicklook.customItems;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import cl.uchile.ing.adi.quicklooklib.fragments.items.BaseItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.fragments.items.VirtualItem;

/**
 * Created by dudu on 18-01-2016.
 */
public class QLItem extends VirtualItem {

    public static  final String QL_BROADCAST = "cl.uchile.ing.adi.QUICKLOOK_REQUEST";

    public QLItem(String path, String mimetype, long size, Bundle extra) {
        super(path,mimetype,size,extra);
    }

    @Override
    public ArrayList<BaseItem> getItemList() {
    ArrayList<BaseItem> itemList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            JSONArray list = (JSONArray)parser.parse(new FileReader(getPath()));
            Iterator<JSONObject> iter = list.iterator();
            while (iter.hasNext()) {
                JSONObject actual = iter.next();
                String path =(String)actual.get("name");
                String type = loadQLType((String)actual.get("mime"), path);
                long size = (Long)actual.get("size");
                Bundle itemExtra = new Bundle();
                itemExtra.putString("json",extra.getString("json"));
                itemExtra.putString("webPath",(String)actual.get("path"));
                itemExtra.putString("mimetype",(String)actual.get("mime"));
                BaseItem newItem = ItemFactory.getInstance().createItem(path, type, size,itemExtra);
                itemList.add(newItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemList;
    }

    public static String loadQLType(String type,String path) {
        if (type.equals(ItemFactory.FOLDER_MIMETYPE)) {
            return type;
        }
        return loadType(path);
    }

    /**
     * Overwriting retrieve because we need the item
     * @param toRetrieve item inside this
     * @param context Current application context
     * @return
     */
    @Override
    public BaseItem retrieve(BaseItem toRetrieve, Context context) {
            Intent intent = new Intent();
            intent.setAction(QL_BROADCAST);
            intent.putExtra("name",toRetrieve.getName());
            intent.putExtra("mime",toRetrieve.getExtra().getString("mimetype"));
            intent.putExtra("path",toRetrieve.getExtra().getString("webPath"));
            intent.putExtra("size",toRetrieve.getSize());
            context.sendBroadcast(intent);
            return null;
    }

    /**
     *  Placeholder
     * @param id Internal identifier of file
     * @param dirpath Output path of the retrieved file
     * @param context Context of application
     * @return
     */
    @Override
    public String retrieveItem(String id, String dirpath, Context context) {
        return null;
    }

    @Override
    public String getName() {
        return getNameFromPath(path);
    }
}
