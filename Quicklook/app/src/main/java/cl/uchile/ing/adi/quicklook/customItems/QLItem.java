package cl.uchile.ing.adi.quicklook.customItems;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.Buffer;
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
            JsonReader reader = new JsonReader(new FileReader(getPath()));
            reader.beginArray();
            while (reader.hasNext()) {
                String mime = "" ,path = "" ,name = "";
                long size = 0L;
                reader.beginObject();
                while(reader.hasNext()){
                    String key = reader.nextName();
                    switch (key) {
                        case "name":
                            name = reader.nextString();
                            break;
                        case "size":
                            size = reader.nextLong();
                            break;
                        case "mime":
                            mime = reader.nextString();
                            break;
                        case "path":
                            path = reader.nextString();
                            break;
                        default:
                            reader.skipValue();
                            break;
                    }
                }
                reader.endObject();
                String type = loadQLType(mime,path);
                Bundle itemExtra = new Bundle();
                itemExtra.putString("webPath",path);
                itemExtra.putString("webMimetype",mime);
                BaseItem newItem = ItemFactory.getInstance().createItem(name, type, size,itemExtra);
                Log.d("QL",""+newItem);
                itemList.add(newItem);
            }
            reader.endArray();
            reader.close();
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
