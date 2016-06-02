package cl.uchile.ing.adi.quicklooklib;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Stack;

import cl.uchile.ing.adi.quicklooklib.fragments.DefaultFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.ListFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment;
import cl.uchile.ing.adi.quicklooklib.items.BaseItem;
import cl.uchile.ing.adi.quicklooklib.items.FileItem;
import cl.uchile.ing.adi.quicklooklib.items.FolderItem;
import cl.uchile.ing.adi.quicklooklib.items.IListItem;
import cl.uchile.ing.adi.quicklooklib.items.VirtualItem;

public class QuicklookActivity extends AppCompatActivity implements ListFragment.OnListFragmentInteractionListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private String path;
    private Runnable r;
    private View coordinator;
    private QuicklookFragment currentFragment;
    private Stack<BaseItem> itemStack = new Stack<>();
    private BaseItem currentItem;
    ProgressDialog pd;
    private static QuicklookConfigurator CONFIGURATOR = null;
    private static String TAG = "QuickLookPermissions";
    public static final String QUICKLOOK_ERROR = "cl.uchile.ing.adi.quicklook.QUICKLOOK_ERROR";

    private static int WRITE_PERMISSIONS = 155;

    boolean isOpeningFiles = true;


    // Activity Config.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set context to items (localization)
        BaseItem.setContext(getApplicationContext());
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
    protected void onResume() {
        super.onResume();
        if(CONFIGURATOR!=null) CONFIGURATOR.registerBroadcasts(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(CONFIGURATOR!=null) CONFIGURATOR.unregisterBroadcasts(this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        if (isOpeningFiles) {
            super.onNewIntent(intent);
            //Set url of start item
            if (pd != null) pd.dismiss();
            BaseItem item = null;
            boolean backstack = false;
            try {
                this.path = intent.getStringExtra("localurl");
                generateFolders();
                if (getIntent() != intent) {
                    backstack = true;
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
                item = ItemFactory.getInstance().createItem(this.path, type, size, extra);
                checkPermissionsAndChangeFragment(item, backstack);
            } catch (Exception e) {
                e.printStackTrace();
                String info = getResources().getString(R.string.quicklook_bad_configuration);
                showInfo(info);
                QuicklookFragment qf = null;
                if (item != null) {
                    qf = item.getFragment();
                }
                reportError(item, qf, info);
            }
        } else {
            // We don't want to open the file, only we need to alert it's downloaded.
            setOpeningFiles(true);
            showInfo(String.format(getResources().getString(R.string.info_document_saved), BaseItem.getDownloadPath()));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        invalidateOptionsMenu();
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
    public void onBackPressed() {
        super.onBackPressed();
        if (!itemStack.empty()) {
            currentItem = itemStack.pop();
            updateActionBar();
        }
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
        if( this.currentFragment == null ) return true;

        BaseItem item = this.getItem();
        // If it's a folder, don't show save as button.
        if(!item.willShowOptionsMenu()) return true;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
        if( ! item.isOpenable() ) menu.findItem(R.id.open_with).setVisible(false);

        return true;
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
        if (item!=null) {
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            if (backstack) {
                t.addToBackStack(null);
                itemStack.add(currentItem);
            }
            if (!(item.openAsDefault() || !item.isOpenable())) {
                item.setFragment(new DefaultFragment());
            }
            setCurrentItem(item);
            setCurrentFragment(item.getFragment());
            t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            t.replace(R.id.quicklook_fragment, currentFragment, "QuickLook");
            t.commitAllowingStateLoss();
            if (!(item.openAsDefault() || !item.isOpenable())) {
                openItem();
            }
            updateActionBar();
        }
    }


    /**
     * Updates... the action bar!
     */
    public void updateActionBar() {
        getSupportActionBar().setTitle(this.getItem().getTitle());
        getSupportActionBar().setSubtitle(this.getItem().getSubTitle());
    }

    /**
     * Shows a snack bar with information.
     * @param info
     */
    public void showInfo(String info) {
        Snackbar.make(coordinator, info,
                Snackbar.LENGTH_LONG).show();
    }

    /**
     * Action when item is retrieved...
     * @param toRetrieve the item which is going to be displayed.
     * @param container item which contains toRetrieve.
     */
    public BaseItem retrieveElement(BaseItem toRetrieve, VirtualItem container) {
        return  container.retrieve(toRetrieve, getApplicationContext());
    }

   // Getters/Setters

    public QuicklookFragment getFragment() {
        return currentFragment;
    }
    
    public BaseItem getItem() {
        return currentItem;
    }

    public void setCurrentFragment(QuicklookFragment fragment) {
        currentFragment = fragment;
    }

    public void setCurrentItem(BaseItem item) {
        currentItem = item;
    }

    // Button item functions

    public Uri saveItem(boolean inform) {
        Uri itemUri =  getItem().save();
        if (itemUri!=null) {
            if (inform)
                showInfo(String.format(getResources().getString(R.string.info_document_saved), BaseItem.getDownloadPath()));
        } else {
            setOpeningFiles(false);
        }
        return itemUri;
    }

    public Uri saveItem() {
        return saveItem(true);
    }

    public void openItem() {
        BaseItem item = getItem();
        if( ! item.isOpenable() ) {
            // TO-DO toast ?
            return;
        }
        Uri pathUri = saveItem(false);
        if (pathUri!=null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String mime = item.getMime();
            intent.setDataAndType(pathUri, mime);
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.quicklook_open)));
        }
    }


    public void shareItem() {
        Uri pathUri = saveItem(false);
        if (pathUri!=null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            File f = new File(pathUri.getPath());
            if (f.exists()) {
                BaseItem item = getItem();
                intent.setType(item.getMime());
                intent.putExtra(Intent.EXTRA_STREAM, pathUri);
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.item_share_title));
                intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.item_share_text));
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.quicklook_share)));
            }
        }
    }

    //Helper (Static) Functions

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

    public static void registerConfigurator(QuicklookConfigurator configurator){
        CONFIGURATOR = configurator;
    }



    /**
     * Sets the download path.
     * @param path
     */
    public static void setDownloadPath(String path) {
        BaseItem.setDownloadPath(path);
    }

    public void removeFromBackStack() {
        if (!itemStack.isEmpty()) {
            FragmentManager f = getSupportFragmentManager();
            f.popBackStack();
            currentItem = itemStack.pop();
        }
    }

    @Override
    public void makeTransition(final BaseItem mItem, final boolean backstack) {
        final IListItem originalItem = (IListItem) (getItem());
        final boolean showProgress = !getItem().willShowOwnProgress();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(showProgress) {
                        pd = ProgressDialog.show(QuicklookActivity.this,
                                getString(R.string.quicklook_loading_file),
                                mItem.getTitle());
                    }
                    BaseItem result = originalItem.onClick(QuicklookActivity.this, mItem);
                    if (result != null) {
                        if (!backstack) {
                            removeFromBackStack();
                        }
                        changeFragment(result);
                        if(pd!=null){pd.dismiss();pd=null;}
                    }
                }
            });
    }

    /** .
     * Opens item with default fragment if anything goes wrong
     * @param item
     */
    public void fragmentFallback(BaseItem item) {
        String info = getResources().getString(R.string.quicklook_item_error);
        if (item!=null) {
            item.setFragment(new DefaultFragment());
            removeFromBackStack();
            changeFragment(item, true);
        } else {
            removeFromBackStack();
            showInfo(info);
        }
        QuicklookFragment qf=null;
        if (item!=null) {
            qf = item.getFragment();
        }
        reportError(item, qf, info);
    }

    /**
     * Allows to report an error via broadcast.
     * @param item current item
     * @param fragment current fragment
     * @param description current description
     */
    public void reportError(BaseItem item, QuicklookFragment fragment, String description) {
        Intent intent = new Intent();
        String itemname = item==null? "Null item" : item.getName();
        String itempath = item==null? "Null item" : item.getPath();
        String itemtype = item==null? "Null item" : item.getType();
        long itemsize = item==null? -1313 : item.getSize();
        String fragname = fragment == null ? "Null Fragment" : fragment.getClass().getName();
        intent.setAction(QUICKLOOK_ERROR);
        String error = "{" +
                        "'description': '"+ description + "'," +
                        "'itemname': '"+ itemname + "'," +
                        "'itempath': '"+ itempath + "'," +
                        "'itemmime': '"+ itemtype + "'," +
                        "'itemsize': '"+ itemsize + "'," +
                        "'fragment': '"+ fragname + "'"+
                "}";
        intent.putExtra("error",error);
        sendBroadcast(intent);
    }

    public void setOpeningFiles(boolean state) {
        isOpeningFiles = state;
    }
}
