package cl.uchile.ing.adi.quicklook;

import android.Manifest;
import android.app.DownloadManager;
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

import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cl.uchile.ing.adi.quicklooklib.QuicklookActivity;
import cl.uchile.ing.adi.quicklook.customItems.QLItem;

public class  MainActivity extends AppCompatActivity implements DemoAssetFragment.OnDemoAssetFragmentListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static String FILES_ASSETS_DIR = "files/";
    Runnable r;
    private static int REQUEST_FILE_PERMISSIONS = 121;
    BroadcastReceiver br;

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
                openIntent(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
            }
        };
        registerReceiver(br, new IntentFilter(QLItem.QL_BROADCAST));
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
        String s = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        QuicklookActivity.registerType("ql", QLItem.class);
        QuicklookActivity.setDownloadPath(s+"/hola");
        startActivity(i);
    }

    public void openDownloads(View v) {
        Intent i = new Intent(this, QuicklookActivity.class);
        String s = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        i.putExtra("localurl", s);
        QuicklookActivity.registerType("ql", QLItem.class);
        QuicklookActivity.setDownloadPath(s+"/hola");
        startActivity(i);
    }

    public void openQL(View v) {
        Intent i = new Intent(this, QuicklookActivity.class);
        String s = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        i.putExtra("localurl", "Prueba.zip.ql");
        Bundle b = new Bundle();
        b.putString("json","[{\"name\":\"Control1Anteriores\\/\",\"mime\":\"application\\/folder\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzLw==\",\"size\":0},{\"name\":\"Control1Anteriores\\/C1 2009 Oto¤o\\/\",\"mime\":\"application\\/folder\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMDkgT3RvpG8v\",\"size\":0},{\"name\":\"Control1Anteriores\\/C1 2009 Oto¤o\\/Pauta Control 1 Oto¤o 2009.pdf\",\"mime\":\"application\\/pdf\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMDkgT3RvpG8vUGF1dGEgQ29udHJvbCAxIE90b6RvIDIwMDkucGRm\",\"size\":432043},{\"name\":\"Control1Anteriores\\/C1 2009 Primavera\\/\",\"mime\":\"application\\/folder\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMDkgUHJpbWF2ZXJhLw==\",\"size\":0},{\"name\":\"Control1Anteriores\\/C1 2009 Primavera\\/IN3501_Control_1_2009_2_Enunciado.pdf\",\"mime\":\"application\\/pdf\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMDkgUHJpbWF2ZXJhL0lOMzUwMV9Db250cm9sXzFfMjAwOV8yX0VudW5jaWFkby5wZGY=\",\"size\":139661},{\"name\":\"Control1Anteriores\\/C1 2009 Primavera\\/IN3501_Control_1_2009_2_Pauta.pdf\",\"mime\":\"application\\/pdf\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMDkgUHJpbWF2ZXJhL0lOMzUwMV9Db250cm9sXzFfMjAwOV8yX1BhdXRhLnBkZg==\",\"size\":201164},{\"name\":\"Control1Anteriores\\/C1 2010 Oto¤o\\/\",\"mime\":\"application\\/folder\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTAgT3RvpG8v\",\"size\":0},{\"name\":\"Control1Anteriores\\/C1 2010 Oto¤o\\/Control 1 oto¤o 2010.pdf\",\"mime\":\"application\\/pdf\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTAgT3RvpG8vQ29udHJvbCAxIG90b6RvIDIwMTAucGRm\",\"size\":252494},{\"name\":\"Control1Anteriores\\/C1 2010 Primavera\\/\",\"mime\":\"application\\/folder\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTAgUHJpbWF2ZXJhLw==\",\"size\":0},{\"name\":\"Control1Anteriores\\/C1 2010 Primavera\\/Pauta_Control_1.pdf\",\"mime\":\"application\\/pdf\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTAgUHJpbWF2ZXJhL1BhdXRhX0NvbnRyb2xfMS5wZGY=\",\"size\":341064},{\"name\":\"Control1Anteriores\\/C1 2011 Oto¤o\\/\",\"mime\":\"application\\/folder\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTEgT3RvpG8v\",\"size\":0},{\"name\":\"Control1Anteriores\\/C1 2011 Oto¤o\\/pauta-control1.pdf\",\"mime\":\"application\\/pdf\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTEgT3RvpG8vcGF1dGEtY29udHJvbDEucGRm\",\"size\":130354},{\"name\":\"Control1Anteriores\\/C1 2011 primavera\\/\",\"mime\":\"application\\/folder\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTEgcHJpbWF2ZXJhLw==\",\"size\":0},{\"name\":\"Control1Anteriores\\/C1 2011 primavera\\/Pregunta 1.pdf\",\"mime\":\"application\\/pdf\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTEgcHJpbWF2ZXJhL1ByZWd1bnRhIDEucGRm\",\"size\":415598},{\"name\":\"Control1Anteriores\\/C1 2011 primavera\\/Pregunta 2.pdf\",\"mime\":\"application\\/pdf\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTEgcHJpbWF2ZXJhL1ByZWd1bnRhIDIucGRm\",\"size\":635642},{\"name\":\"Control1Anteriores\\/C1 2012 primavera\\/\",\"mime\":\"application\\/folder\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTIgcHJpbWF2ZXJhLw==\",\"size\":0},{\"name\":\"Control1Anteriores\\/C1 2012 primavera\\/Pauta P1.doc\",\"mime\":\"application\\/msword\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTIgcHJpbWF2ZXJhL1BhdXRhIFAxLmRvYw==\",\"size\":63488},{\"name\":\"Control1Anteriores\\/C1 2012 primavera\\/Pauta P2.doc\",\"mime\":\"application\\/msword\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTIgcHJpbWF2ZXJhL1BhdXRhIFAyLmRvYw==\",\"size\":62464},{\"name\":\"Control1Anteriores\\/C1 2013 oto¤o\\/\",\"mime\":\"application\\/folder\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTMgb3RvpG8v\",\"size\":0},{\"name\":\"Control1Anteriores\\/C1 2013 oto¤o\\/C1 Pauta.pdf\",\"mime\":\"application\\/pdf\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTMgb3RvpG8vQzEgUGF1dGEucGRm\",\"size\":128358},{\"name\":\"Control1Anteriores\\/C1 2014 oto¤o\\/\",\"mime\":\"application\\/folder\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTQgb3RvpG8v\",\"size\":0},{\"name\":\"Control1Anteriores\\/C1 2014 oto¤o\\/c1 enun otono 2014.pdf\",\"mime\":\"application\\/pdf\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTQgb3RvpG8vYzEgZW51biBvdG9ubyAyMDE0LnBkZg==\",\"size\":147563},{\"name\":\"Control1Anteriores\\/C1 2014 primavera\\/\",\"mime\":\"application\\/folder\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTQgcHJpbWF2ZXJhLw==\",\"size\":0},{\"name\":\"Control1Anteriores\\/C1 2014 primavera\\/c1 in3501 prim 2014.pdf\",\"mime\":\"application\\/pdf\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTQgcHJpbWF2ZXJhL2MxIGluMzUwMSBwcmltIDIwMTQucGRm\",\"size\":182848},{\"name\":\"Control1Anteriores\\/C1 2014 primavera\\/Pauta_Control_1prim2014.pdf\",\"mime\":\"application\\/pdf\",\"path\":\"https:\\/\\/www.u-cursos.cl\\/demo\\/2015\\/0\\/ADKINTUN\\/1\\/material_docente\\/bajar?id_material=1261437&file=Q29udHJvbDFBbnRlcmlvcmVzL0MxIDIwMTQgcHJpbWF2ZXJhL1BhdXRhX0NvbnRyb2xfMXByaW0yMDE0LnBkZg==\",\"size\":544256}]");
        i.putExtra("extra", b);
        QuicklookActivity.registerType("ql", QLItem.class);
        QuicklookActivity.setDownloadPath(s+"/hola");
        startActivity(i);
    }

    private boolean copyAssetToDownload(String assetPath, String assetName, File destinationFile){
        try {
            FileOutputStream fos = new FileOutputStream(destinationFile);
            IOUtils.copy(getAssets().open(assetPath + assetName), fos);
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

    @Override
    protected void onDestroy() {
        super.onPause();
        unregisterReceiver(br);
    }
}
