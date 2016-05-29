package cl.uchile.ing.adi.quicklook;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import cl.uchile.ing.adi.quicklooklib.QuicklookActivity;
import cl.uchile.ing.adi.quicklook.customItems.QLItem;

public class  MainActivity extends AppCompatActivity implements DemoAssetFragment.OnDemoAssetFragmentListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static String FILES_ASSETS_DIR = "files/";

    private static final String QUICKLOOK_ERROR = "cl.uchile.ing.adi.quicklook.QUICKLOOK_ERROR";

    Runnable r;
    private static int REQUEST_FILE_PERMISSIONS = 121;
    BroadcastReceiver br, as;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = DemoAssetFragment.newInstance();
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.main_activity_fragment, fragment, "MainQuickLook");
        t.commit();
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("MainActivity", "Oh, un broadcast... abrire quicklook de nuevo");
                Log.d("MainActivity", intent.getStringExtra("path"));
                openIntent(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),null);
            }
        };
        registerReceiver(br, new IntentFilter(QLItem.QL_BROADCAST));
        as = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("MainActivity", "Oh, un broadcast de error");
                Log.d("MainActivity", intent.getStringExtra("error"));
            }
        };
        registerReceiver(as, new IntentFilter(QUICKLOOK_ERROR));

    }

    @Override public void onAssetSelected(final String item) {
        final File downloadAsset = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/" + item);
        r = new Runnable() {
            public void run() {
                String urlForAsset = "";
                String mimetype = null;
                if(downloadAsset.exists()) {
                    urlForAsset = downloadAsset.getAbsolutePath();
                }
                else{
                    if(copyAssetToDownload(FILES_ASSETS_DIR, item,downloadAsset)) {
                        urlForAsset = downloadAsset.getAbsolutePath();
                    }
                }
                HashMap<String,String> extensions = new HashMap<>();
                extensions.put("c","text/c");
                extensions.put("zxc","image/zxc");
                //extensions.put("json","application/json");
                String[] extsplit = urlForAsset.split("\\.");
                String ext = extsplit[extsplit.length-1];
                if (extensions.containsKey(ext)) {
                    mimetype = extensions.get(ext);
                }
                openIntent(urlForAsset, mimetype);
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

    public void openIntent(String urlForAsset,String mimetype) {
        Intent i = new Intent(this, QuicklookActivity.class);
        i.putExtra("localurl", urlForAsset);
        if (mimetype!=null) {
            Bundle b = new Bundle();
            b.putString("mime-type",mimetype);
            i.putExtra("extra", b);
        }
        String s = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        QuicklookActivity.registerType(QLItem.class, "ql");
        QuicklookActivity.setDownloadPath(s + "/hola/");
        startActivity(i);
    }

    public void openDownloads(View v) {
        Intent i = new Intent(this, QuicklookActivity.class);
        String s = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        i.putExtra("localurl", s);
        QuicklookActivity.registerType(QLItem.class, "ql");
        QuicklookActivity.setDownloadPath(s+"/hola/");
        startActivity(i);
    }

    private boolean copyAssetToDownload(String assetPath, String assetName, File destinationFile){
        try {
            FileOutputStream fos = new FileOutputStream(destinationFile);
            int copied = IOUtils.copy(getAssets().open(assetPath+assetName), fos);
            fos.close();
            return true;
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
                Toast.makeText(this, "Permisos concedidos :D",Toast.LENGTH_LONG).show();
                r.run();
            } else {
                Log.i("quicklook66", "WRITE_EXTERNAL permission was NOT granted.");
                Toast.makeText(this, "Permisos no concedidos :(",Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
        unregisterReceiver(as);
    }
}
