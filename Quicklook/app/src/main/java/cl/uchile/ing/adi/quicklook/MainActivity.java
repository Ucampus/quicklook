package cl.uchile.ing.adi.quicklook;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
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
    private static String FILES_ASSETS_DIR = "files/";
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = DemoAssetFragment.newInstance();
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.main_activity_fragment, fragment, "MainQuickLook");
        t.commit();
    }

    @Override public void onAssetSelected(String item) {
        String urlForAsset = urlForAsset(item);
        Intent i = new Intent(this, QuicklookActivity.class);
        i.putExtra("localurl", urlForAsset);
        startActivity(i);
    }

    private String urlForAsset(String assetName){
        File downloadAsset = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/" + assetName);

        if(downloadAsset.exists()) {
            return downloadAsset.getAbsolutePath();
        }
        else{
            if(copyAssetToDownload(FILES_ASSETS_DIR, assetName,downloadAsset)) return downloadAsset.getAbsolutePath();
        }
        return null;
    }

    private boolean copyAssetToDownload(String assetPath, String assetName, File destinationFile){
        try {
            FileOutputStream fos = new FileOutputStream(destinationFile);
            int copied = IOUtils.copy(getAssets().open(assetPath+assetName), fos);
            fos.close();
            if(copied>0) {
                DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                dm.addCompletedDownload(assetName,assetName, true, mime(destinationFile), destinationFile.getAbsolutePath(), copied, false);
                return true;
            }
            else return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String mime(File file){
        String[] pieces = file.getName().split("\\.");
        String ext = pieces[pieces.length-1];
        if(ext.equals("png")) return "image/png";
        if(ext.equals("pdf")) return "application/pdf";
        if(ext.equals("txt")) return "text/plain";
        if(ext.equals("zip")) return "application/zip";
        return "text/plain";
    }

}
