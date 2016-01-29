package cl.uchile.ing.adi.quicklooklib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import cl.uchile.ing.adi.quicklooklib.items.IListItem;
import cl.uchile.ing.adi.quicklooklib.items.VirtualItem;

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
        // Set context to items (localization)
        BaseItem.setContext(this);
        setContentView(R.layout.activity_quicklook);
        coordinator = findViewById(R.id.quicklook_coordinator);
        //Only if fragment is not rendered
        if (savedInstanceState==null) {
            onNewIntent(getIntent());
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
        super.onNewIntent(intent);
        //Set url of start item
        boolean backstack = false;
        this.path = intent.getStringExtra("localurl");
        generateFolders();
        if (getIntent()!=intent) {
            backstack=true;
        }
        setIntent(intent);
        long size = BaseItem.getSizeFromPath(this.path);
        String type = FileItem.loadFileType(new File(this.path));
        Bundle extra;
        if (getIntent().hasExtra(BaseItem.ITEM_EXTRA)) {
            extra = getIntent().getBundleExtra(BaseItem.ITEM_EXTRA);
        } else {
            extra = new Bundle();
        }
        BaseItem item = ItemFactory.getInstance().createItem(this.path, type, size,extra);
        checkPermissionsAndChangeFragment(item,backstack);
    }

    private void generateFolders() {
        //Generate download folder
        if (BaseItem.getDownloadPath() == null) {
            BaseItem.setDownloadPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .getAbsolutePath()+"/Quicklook/");
        }
        String downloadPath = BaseItem.getDownloadPath();
        //Create downloadPath folder if not exists.
        File downloadFolder = new File(downloadPath);
        if (!downloadFolder.exists()) downloadFolder.mkdirs();
        BaseItem.setDownloadPath(downloadPath);

        //Generate cache folder
        if (BaseItem.getCachePath()==null) {
            BaseItem.setCachePath(getFilesDir().getAbsolutePath() + "/quicklook/");
        }
        //Create cachePath folder if not exists.
        String cachePath=BaseItem.getCachePath();
        File cacheFolder = new File(cachePath);
        if (!cacheFolder.exists()) cacheFolder.mkdirs();
        BaseItem.setCachePath(cachePath);
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
        QuicklookFragment f = this.current;
        if (!(f == null || f.getItem() instanceof IListItem)) {
            inflater.inflate(R.menu.item_menu, menu);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String innerPath = BaseItem.getCachePath();
        File f = new File(innerPath);
        try {
            FileUtils.deleteDirectory(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == WRITE_PERMISSIONS) {
            // Received permission result for storage permission.

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // storage permission has been granted, preview can be displayed
                r.run();
            } else {
                Snackbar.make(coordinator, getResources().getString(R.string.quicklook_permission_error),
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
    private void checkPermissionsAndChangeFragment(final BaseItem item, final boolean addToBackstack) {
        r = new Runnable(){
            public void run() {
                changeFragment(item,addToBackstack);
                invalidateOptionsMenu();
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_PERMISSIONS);
        } else {
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
        setFragment(item.getFragment());
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        t.replace(R.id.quicklook_fragment, current, "QuickLook");
        if (backstack) {
            t.addToBackStack(null);
        }
        t.commitAllowingStateLoss();
        updateActivity(item);
    }


    /**
     * Updates... the action bar!
     * @param item Item with new info for the actionbar.
     */
    private void updateActivity(BaseItem item) {
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
        setFragment(item.getFragment());
        updateActivity(item);
    }

    /**
     * Shows a snack bar with information.
     * @param info
     */
    public void onListFragmentInfo(String info) {
        Snackbar.make(coordinator, info,
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

    public Uri saveItem(boolean inform) {
        BaseItem item = current.getItem();
        String mime =getMime(item.getPath());
        String newPath = item.copyItem(mime);
        Uri pathUri = Uri.parse("file://" + newPath);
        if (inform) onListFragmentInfo(String.format(getResources().getString(R.string.info_document_saved), BaseItem.getDownloadPath()));
        return pathUri;
    }

    public Uri saveItem() {
        return saveItem(true);
    }

    public void openItem() {
        Uri pathUri = saveItem(false);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mime = getMime(pathUri.getPath());
        intent.setDataAndType(pathUri, mime);
        startActivity(Intent.createChooser(intent, "Open"));
    }


    public void shareItem() {
        Uri pathUri = saveItem(false);
        Intent intent = new Intent(Intent.ACTION_SEND);
        File f = new File(pathUri.getPath());
        if (f.exists()) {
            String mime = getMime(pathUri.getPath());
            intent.setType(mime);
            intent.putExtra(Intent.EXTRA_STREAM, pathUri);
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.item_share_title));
            intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.item_share_text));
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
     * @param className
     * @param types
     */
    public static void registerType(Class className, String... types) {
        for (String type:types) {
            ItemFactory.getInstance().register(className, type);
        }
    }

    /**
     * Sets the download path.
     * @param path
     */
    public static void setDownloadPath(String path) {
        BaseItem.setDownloadPath(path);
    }

    public void removeFromBackStack(QuicklookFragment frag) {
        FragmentManager f = getSupportFragmentManager();
        f.popBackStack();
    }
}
