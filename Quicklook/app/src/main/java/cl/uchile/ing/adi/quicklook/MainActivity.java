package cl.uchile.ing.adi.quicklook;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import cl.uchile.ing.adi.quicklooklib.QuicklookActivity;

public class MainActivity extends AppCompatActivity implements DemoAssetFragment.OnDemoAssetFragmentListener {
    private static String FILES_ASSETS_DIR = "files";
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = DemoAssetFragment.newInstance();
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.main_activity_fragment , fragment, "MainQuickLook");
        t.commit();
    }

    @Override public void onAssetSelected(String item) {
        String urlForAsset = urlForAsset(item);
        Intent i = new Intent(this, QuicklookActivity.class);
        String s = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        i.putExtra("url", urlForAsset);
        startActivity(i);
    }

    private String urlForAsset(String assetName){
        File downloadAsset = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/" + assetName);
        if(downloadAsset.exists()) {
            return downloadAsset.getAbsolutePath();
        }
        else{
            if(copyAssetToDownload(FILES_ASSETS_DIR+"/"+assetName,downloadAsset)) return downloadAsset.getAbsolutePath();
        }
        return null;
    }

    private boolean copyAssetToDownload(String assetName, File destinationFile){
        try {
            FileOutputStream fos = new FileOutputStream(destinationFile);
            int copied = IOUtils.copy(getAssets().open(assetName), fos);
            fos.close();
            if(copied>0) return true;
            else return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
