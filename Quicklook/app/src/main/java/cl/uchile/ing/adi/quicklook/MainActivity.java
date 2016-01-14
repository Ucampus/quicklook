package cl.uchile.ing.adi.quicklook;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import cl.uchile.ing.adi.quicklooklib.QuicklookActivity;

public class MainActivity extends AppCompatActivity implements DemoAssetFragment.OnDemoAssetFragmentListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static String FILES_ASSETS_DIR = "files/";
    Runnable r;
    private static int REQUEST_FILE_PERMISSIONS = 121;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = DemoAssetFragment.newInstance();
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.main_activity_fragment, fragment, "MainQuickLook");
        t.commit();
    }

    @Override public void onAssetSelected(final String item) {
        final File downloadAsset = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/" + item);
        r = new Runnable() {
            public void run() {
                String urlForAsset = null;
                if(downloadAsset.exists()) {
                    urlForAsset = downloadAsset.getAbsolutePath();
                }
                else{
                    if(copyAssetToDownload(FILES_ASSETS_DIR, item,downloadAsset)) {
                        urlForAsset = downloadAsset.getAbsolutePath();
                    }
                }
                openIntent(urlForAsset);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Storage permission has not been granted.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_FILE_PERMISSIONS);
        } else {

            // Storage permissions are already available, show the camera preview.
            Log.i("quicklook66",
                    "STORAGE permission has already been granted. Displaying files.");
            r.run();
        }

    }

    public void openIntent(String urlForAsset) {
        Intent i = new Intent(this, QuicklookActivity.class);
        i.putExtra("localurl", urlForAsset);
        startActivity(i);
    }

    public void openDownloads(View v) {
        Intent i = new Intent(this, QuicklookActivity.class);
        String s = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        i.putExtra("localurl", s);
        startActivity(i);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_FILE_PERMISSIONS) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i("quicklook66", "Received response for Write External permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i("quicklook66", "WRITE_EXTERNAL permission has now been granted. Showing preview.");
                Toast.makeText(this, "Permisos concedidos :D",Toast.LENGTH_LONG);
                r.run();
            } else {
                Log.i("quicklook66", "WRITE_EXTERNAL permission was NOT granted.");
                Toast.makeText(this, "Permisos no concedidos :(",Toast.LENGTH_LONG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
