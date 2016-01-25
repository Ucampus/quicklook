package cl.uchile.ing.adi.quicklooklib;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.items.BaseItem;
import cl.uchile.ing.adi.quicklooklib.fragments.ListFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.items.FileItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.FolderItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.fragments.items.VirtualItem;

public class QuicklookActivity extends AppCompatActivity implements ListFragment.OnListFragmentInteractionListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private String path;
    private static String TAG = "QuickLookPermissions";
    private Runnable r;
    private View coordinator;
    private static int WRITE_PERMISSIONS = 155;
    private QuicklookFragment current;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quicklook);
        coordinator = findViewById(R.id.quicklook_coordinator);
        onNewIntent(getIntent());
        if (savedInstanceState==null) {
            long size = BaseItem.getSizeFromPath(this.path);
            String type = FileItem.loadFileType(new File(this.path));
            Bundle extra;
            if (getIntent().hasExtra("extra")) {
                extra = getIntent().getBundleExtra("extra");
            } else {
                extra = new Bundle();
            }
            BaseItem item = ItemFactory.getInstance().createItem(this.path, type, size,extra);
            checkPermissionsAndChangeFragment(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        this.path = intent.getStringExtra("localurl");

        //Set download path
        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath()+"/Quicklook/";
        if (intent.hasExtra("downloadpath")) {
            downloadPath = intent.getStringExtra("downloadpath");
        }
        File folder = new File(downloadPath);
        if (!folder.exists()) folder.mkdirs();
        BaseItem.setDownloadPath(downloadPath);

        //Action bar back button
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Copies an item on internal space to download folder.
     * @param item Item to be copied
     * @return Path of item on downloads folder.
     */
    private String copyItemToDownloadFolder(BaseItem item,String mime) {
        try {
            String itemPath = BaseItem.getDownloadPath()+item.getName();
            File f = new File(itemPath);
            int copied = 1;
            if (!f.exists()) {
                FileOutputStream fos = new FileOutputStream(itemPath);
                copied = IOUtils.copy(new FileInputStream(item.getPath()), fos);
                fos.close();
                if(copied>0) {
                    DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                        dm.addCompletedDownload(item.getName(),item.getName(), true, mime, itemPath, copied, false);
                    }
                } else{
                    return null;
                }
            }
            return itemPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void checkPermissionsAndChangeFragment(final BaseItem item) {
        r = new Runnable(){
            public void run() {
                changeFragment(item,false);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(TAG, "Storage permissions has NOT been granted. Requesting permissions.");
            requestStoragePermissions();
        } else {

            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(TAG,
                    "Contact permissions have already been granted. Displaying contact details.");
            r.run();
        }
    }

    /**
     * Manages the transition between the fragments which shows the items.
     * @param item Item to show.
     */
    public void changeFragment(BaseItem item) {
        changeFragment(item, true);
    }

    /**
     * Manages the transition between the fragments which shows the items.
     * @param item Item to show.
     * @param backstack Adds the previous fragment to backstack.
     */
    public void changeFragment(BaseItem item, boolean backstack){
        updateActivity(item);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.quicklook_fragment, current, "QuickLook");
        if (backstack) t.addToBackStack(null);
        t.commitAllowingStateLoss();
    }

    /**
     * Method called by fragment when item is clicked on list view.
     * @param item the item which is going to be displayed.
     */

    public void onListFragmentInteraction(BaseItem item) {
        changeFragment(item);
    }

    /**
     * Manages the text in Action Bar, with current path in filesystem.
     * @param item the item which is going to be displayed
     */
    public void onListFragmentCreation(BaseItem item) {
        updateActivity(item);
    }

    public void onListFragmentRetrieval(BaseItem toRetrieve, VirtualItem container) {
        BaseItem retrieved = container.retrieve(toRetrieve, getApplicationContext());
        if (retrieved!=null) {
            changeFragment(retrieved);
        } else {
            onListFragmentInfo("Nothing to show!");
        }
    }

    /**
     * Updates... the action bar!
     * @param item Item with new info for the actionbar.
     */
    private void updateActivity(BaseItem item) {
        setFragment(item.getFragment());
        getSupportActionBar().setTitle(item.getTitle());
        getSupportActionBar().setSubtitle(item.getSubTitle());

    }

    private void requestStoragePermissions() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                WRITE_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == WRITE_PERMISSIONS) {
            // Received permission result for storage permission.
            Log.i(TAG, "Received response for storage permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // storage permission has been granted, preview can be displayed
                Log.i(TAG, "STORAGE permission has now been granted. Showing preview.");
                r.run();
            } else {
                Log.i(TAG, "STORAGE permission was NOT granted.");
                Snackbar.make(coordinator, "Permission error. Can't show files.",
                        Snackbar.LENGTH_SHORT).show();

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public QuicklookFragment getFragment() {
        return current;
    }

    public void setFragment(QuicklookFragment fragment) {
        current = fragment;
    }

    public void onListFragmentInfo(String error) {
        Snackbar.make(coordinator, "Info: "+error,
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mItem) {
        // handle item selection
        int i = mItem.getItemId();
        if (i == android.R.id.home) {
            onBackPressed();
            return true;
        }
        else if (i == R.id.save) {
            Uri pathUri = saveItem();
            return true;
        } else if (i == R.id.share) {
            shareItem();
            return true;
        } else if (i == R.id.open_with) {
            openItem();
            return true;
        } else {
            return super.onOptionsItemSelected(mItem);
        }
    }

    public static void registerType(String type, Class className) {
        ItemFactory.getInstance().register(type, className);
    }

    public static void setDownloadPath(String path) {
        BaseItem.setDownloadPath(path);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (!(this.current.getItem() instanceof FolderItem)) {
            inflater.inflate(R.menu.item_menu, menu);
        }
        return true;
    }

    public Uri saveItem() {
        BaseItem item = current.getItem();
        String mime =getMime(item.getPath());
        String newPath = copyItemToDownloadFolder(item, mime);
        Uri pathUri = Uri.parse("file://" + newPath);
        onListFragmentInfo("Document saved on "+BaseItem.getDownloadPath()+" folder.");
        return pathUri;
    }

    public void openItem() {
        Uri pathUri = saveItem();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mime = getMime(pathUri.getPath());
        intent.setDataAndType(pathUri, mime);
        Log.d("openItem", pathUri.getPath());
        startActivity(Intent.createChooser(intent, "Open"));
    }


    private void shareItem() {
        Uri pathUri = saveItem();
        Intent intent = new Intent(Intent.ACTION_SEND);
        File f = new File(pathUri.getPath());
        if (f.exists()) {
            String mime = getMime(pathUri.getPath());
            intent.setType(mime);
            intent.putExtra(Intent.EXTRA_STREAM, pathUri);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing Quicklook file");
            intent.putExtra(Intent.EXTRA_TEXT,"Hi, I'm sending you a file... Regards!");
            Log.d("shareItem", pathUri.getPath());
            startActivity(Intent.createChooser(intent, "Share"));
        } else {

        }
    }

    private String getMime(String path) {
        String type = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path));
        if (type==null) {
            return "text/plain";
        } else {
            return type;
        }
    }


}
