package cl.uchile.ing.adi.quicklook.customItems;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import cl.uchile.ing.adi.quicklooklib.fragments.items.AItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.fragments.items.VirtualItem;

/**
 * Created by dudu on 18-01-2016.
 */
public class QLItem extends VirtualItem {

    public QLItem(String path, String mimetype, long size, Bundle extra) {
        super(path,mimetype,size,extra);
    }

    @Override
    public ArrayList<AItem> getItemList() {
        ArrayList<AItem> itemList = new ArrayList<>();
        if (extra != null && extra.getString("json")!=null) {
            JSONParser parser = new JSONParser();
            try {
                JSONArray list = (JSONArray)parser.parse(extra.getString("json"));
                Iterator<JSONObject> iter = list.iterator();
                while (iter.hasNext()) {
                    JSONObject actual = iter.next();
                    String path =(String)actual.get("name");
                    String type = loadQLType((String)actual.get("mime"), path);
                    long size = (Long)actual.get("size");
                    Bundle itemExtra = new Bundle();
                    itemExtra.putString("json",extra.getString("json"));
                    itemExtra.putString("webPath",(String)actual.get("path"));
                    AItem newItem = ItemFactory.getInstance().createItem(path, type, size,itemExtra);
                    itemList.add(newItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d("QLItem", "No se pudo leer el json... Tal vez no existe?");
        return itemList;
    }

    public static String loadQLType(String type,String path) {
        if (type.equals(ItemFactory.FOLDER_MIMETYPE)) {
            return type;
        }
        return loadType(path);
    }

    @Override
    public String retrieveItem (String path, String dirpath, Context context) {
        return null;
    }

    @Override
    public String getName() {
        return getNameFromPath(path);
    }
}
