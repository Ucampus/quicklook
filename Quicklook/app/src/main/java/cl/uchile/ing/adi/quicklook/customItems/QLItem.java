package cl.uchile.ing.adi.quicklook.customItems;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.items.BaseItem;
import cl.uchile.ing.adi.quicklooklib.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.items.VirtualItem;


public class QLItem extends VirtualItem {

    public static  final String QL_BROADCAST = "cl.uchile.ing.adi.QUICKLOOK_REQUEST";

    public QLItem(String path, String mimetype, long size, Bundle extra) {
        super(path,mimetype,size,extra);
    }

    @Override
    public ArrayList<BaseItem> getItemList() {
        ArrayList<BaseItem> itemList = new ArrayList<>();
        try {
            String s="";
            BufferedReader br = new BufferedReader(new FileReader(getPath()));
            String line;
            while ((line=br.readLine())!=null) {
                s+=line;
            }
            JSONArray list = new JSONArray(s);
            for (int i=0;i<list.length();i++) {
                JSONObject actual = list.getJSONObject(i);
                String path = actual.getString("name");
                String type = loadQLType(actual.getString("mime"), path);
                long size = actual.getLong("size");
                Bundle itemExtra = new Bundle();
                itemExtra.putString("webPath",actual.getString("path"));
                itemExtra.putString("webMimetype",actual.getString("mime"));
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
     * @return null because Item is going to be retrieved by broadcast receiver.
     */
    @Override
    public BaseItem retrieve(BaseItem toRetrieve, Context context) {
        Intent intent = new Intent();
        intent.setAction(QL_BROADCAST);
        intent.putExtra("name",toRetrieve.getName());
        intent.putExtra("mime",toRetrieve.getExtra().getString("webMimetype"));
        intent.putExtra("path",toRetrieve.getExtra().getString("webPath"));
        intent.putExtra("size",toRetrieve.getSize());
        context.sendBroadcast(intent);
        return null;
    }

    /**
     *  Placeholder because we needed the entire item.
     * @param id Internal identifier of file
     * @param dirpath Output path of the retrieved file
     * @param context Context of application
     * @return null because the item is not going to be retrieved by us.
     */
    @Override
    public String retrieveItem(String id, String dirpath, Context context) {
        return null;
    }

    @Override
    public String getTitle() {
        return getName().split("\\.ql")[0];
    }
}
