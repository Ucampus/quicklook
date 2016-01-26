package cl.uchile.ing.adi.quicklooklib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;

import java.io.File;

import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment;
import cl.uchile.ing.adi.quicklooklib.items.BaseItem;
import cl.uchile.ing.adi.quicklooklib.fragments.ListFragment;
import cl.uchile.ing.adi.quicklooklib.items.FileItem;
import cl.uchile.ing.adi.quicklooklib.items.FolderItem;
import cl.uchile.ing.adi.quicklooklib.items.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.items.VirtualItem;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

public class QuicklookActivity extends AppCompatActivity implements ListFragment.OnListFragmentInteractionListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private String path;
    private Runnable r;
    private View coordinator;
    private QuicklookFragment current;

    private static String TAG = "QuickLookPermissions";
    private static int WRITE_PERMISSIONS = 155;


    // Activity Config.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create Quicklook internal work directory and set it
        if (BaseItem.getCachePath()==null) {
            BaseItem.setCachePath(getFilesDir().getAbsolutePath() + "/quicklook/");
        }
        File f = new File(BaseItem.getCachePath());
        f.mkdirs();
        // Set context to items (localization)
        BaseItem.setContext(this);
        setContentView(R.layout.activity_quicklook);
        coordinator = findViewById(R.id.quicklook_coordinator);
        onNewIntent(getIntent());
        //Only if fragment is not rendered
        if (savedInstanceState==null) {
            long size = BaseItem.getSizeFromPath(this.path);
            String type = FileItem.loadFileType(new File(this.path));
            Bundle extra;
            if (getIntent().hasExtra(BaseItem.ITEM_EXTRA)) {
                extra = getIntent().getBundleExtra(BaseItem.ITEM_EXTRA);
            } else {
                extra = new Bundle();
            }
            BaseItem item = ItemFactory.getInstance().createItem(this.path, type, size,extra);
            checkPermissionsAndChangeFragment(item);
        }
        //Action bar back button
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        //Set url of start item
        this.path = intent.getStringExtra("localurl");
        //Set download path
        String downloadPath;
        if (intent.hasExtra("downloadpath")) {
            downloadPath = intent.getStringExtra("downloadpath");
        } else {
            downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .getAbsolutePath()+"/Quicklook/";
        }
        //Create downloadPath folder if not exists.
        File folder = new File(downloadPath);
        if (!folder.exists()) folder.mkdirs();
        BaseItem.setDownloadPath(downloadPath);
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
            saveItem();
            onListFragmentInfo("Document saved on " + BaseItem.getDownloadPath() + " folder.");
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (!(this.current.getItem() instanceof FolderItem)) {
            inflater.inflate(R.menu.item_menu, menu);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String innerPath = getFilesDir().getAbsolutePath()+"/quicklook/";
        File f = new File(innerPath);
        try {
            FileUtils.deleteDirectory(f);
            Log.d("QuicklookActivity", "Directorio temporal borrado!");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    // Fragment management.

    /**
     * Checks if storage permissions exist
     * @param item Item to render after checking permissions.
     */
    private void checkPermissionsAndChangeFragment(final BaseItem item) {
        r = new Runnable(){
            public void run() {
                changeFragment(item,false);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Storage permissions has NOT been granted. Requesting permissions.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_PERMISSIONS);
        } else {
            Log.i(TAG,
                       "Storage permissions have already been granted. Displaying details.");
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
     * @param backstack if true, adds the previous fragment to backstack.
     */
    public void changeFragment(BaseItem item, boolean backstack){
        updateActivity(item);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.quicklook_fragment, current, "QuickLook");
        if (backstack) t.addToBackStack(null);
        t.commitAllowingStateLoss();
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

    //Listeners

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

    /**
     * Shows a snack bar with information.
     * @param info
     */
    public void onListFragmentInfo(String info) {
        Snackbar.make(coordinator, "Info: "+info,
                Snackbar.LENGTH_LONG).show();
    }

    /**
     * Action when item is retrieved... ¿Can we join it with interaction?
     * @param toRetrieve the item which is going to be displayed.
     * @param container item which contains toRetrieve.
     */
    public void onListFragmentRetrieval(BaseItem toRetrieve, VirtualItem container) {
        BaseItem retrieved = container.retrieve(toRetrieve, getApplicationContext());
        if (retrieved!=null) {
            changeFragment(retrieved);
        }
    }

   // Getters/Setters

    public QuicklookFragment getFragment() {
        return current;
    }

    public void setFragment(QuicklookFragment fragment) {
        current = fragment;
    }

    // Button item functions

    public Uri saveItem() {
        BaseItem item = current.getItem();
        String mime =getMime(item.getPath());
        String newPath = item.copyItem(mime);
        Uri pathUri = Uri.parse("file://" + newPath);
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

    //Helper (Static) Functions

    /**
     * Returns mime type of file, or "text/plain" if it isn't detected.
     * @param path Path to file.
     * @return Mime type.
     */
    public static String getMime(String path) {
        String type = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path));
        if (type==null) {
            return "text/plain";
        } else {
            return type;
        }
    }

    /**
     * Registers a type to open.
     * @param type
     * @param className
     */
    public static void registerType(String type, Class className) {
        ItemFactory.getInstance().register(type, className);
    }

    /**
     * Sets the download path.
     * @param path
     */
    public static void setDownloadPath(String path) {
        BaseItem.setDownloadPath(path);
    }
}
