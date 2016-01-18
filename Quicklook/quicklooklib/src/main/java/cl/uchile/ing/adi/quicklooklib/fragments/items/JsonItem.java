package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.content.Context;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by dudu on 18-01-2016.
 */
public class JsonItem extends VirtualItem {

    public JsonItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
    }

    @Override
    public ArrayList<AbstractItem> getItemList() {
        ArrayList<AbstractItem> itemList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            JSONArray list = (JSONArray)parser.parse(new FileReader(path));
            Iterator<JSONObject> iter = list.iterator();
            while (iter.hasNext()) {
                JSONObject actual = iter.next();
                String name =(String)actual.get("name");
                String path =(String)actual.get("path");
                String mimetype = (String)actual.get("mime");
                long size = (Long)actual.get("size");
                itemList.add(addToList(path,mimetype,name,size));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemList;
    }

    @Override
    public AbstractItem retrieve(Context context) {
        return null;
    }
}
