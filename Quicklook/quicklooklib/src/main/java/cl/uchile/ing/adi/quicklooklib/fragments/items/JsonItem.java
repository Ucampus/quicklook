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

    public JsonItem() {
    }

    @Override
    public AbstractItem create(String path, String mimetype) {
        String[] newpath = splitVirtualPath(path);
        return new JsonItem(newpath[0],mimetype,newpath[1]);    }

    @Override
    public AbstractItem create(String path, String mimetype, String name, long size) {
        String[] newpath = splitVirtualPath(path);
        return new ZipItem(newpath[0],mimetype,name,size,newpath[1]);
    }

    public JsonItem(String path, String mimetype, String virtualPath) {
        super(path, mimetype, virtualPath);
    }

    public JsonItem(String path, String mimetype, String name, long size, String virtualPath) {
        super(path,mimetype,name,size,virtualPath);
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
