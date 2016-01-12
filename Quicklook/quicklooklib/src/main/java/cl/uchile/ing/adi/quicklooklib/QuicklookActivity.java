package cl.uchile.ing.adi.quicklooklib;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cl.uchile.ing.adi.quicklooklib.fragments.items.AbstractItem;
import cl.uchile.ing.adi.quicklooklib.fragments.FolderFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.items.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.fragments.items.ZipItem;

public class QuicklookActivity extends AppCompatActivity implements FolderFragment.OnListFragmentInteractionListener {
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quicklook);

        this.url = getIntent().getStringExtra("localurl");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url.replace("https", "http")));
                startActivity(i);
            }
        });
        String type = AbstractItem.loadMimeType(this.url);
        AbstractItem item = ItemFactory.getInstance().createItem(this.url,type);
        changeFragment(item,false);
    }

    /**
     * Manages the transition between the fragments which shows the items.
     * @param item Item to show.
     */
    public void changeFragment(AbstractItem item) {
        changeFragment(item, true);
    }

    /**
     * Manages the transition between the fragments which shows the items.
     * @param item Item to show.
     * @param backstack Adds the previous fragment to backstack.
     */
    public void changeFragment(AbstractItem item, boolean backstack){
        Fragment fragment = item.getFragment();
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.quicklook_fragment, fragment, "QuickLook");
        if (backstack) t.addToBackStack(null);
        t.commit();
    }

    /**
     * Method called by fragment when item is clicked on list view.
     * @param item the item which is going to be displayed.
     */
    public void onListFragmentInteraction(AbstractItem item) {
        changeFragment(item);

    }

    /**
     * Manages the text in Action Bar, with current path in filesystem.
     * @param item the item which is going to be displayed
     */
    public void onListFragmentCreation(AbstractItem item) {
        updateActionBar(item);
    }

    /**
     * Manages the extraction of elements in compressed files.
     * Also shows them after extraction.
     * @param item the item which is going to be displayed.
     */
    public void onListFragmentExtraction(ZipItem item) {
        AbstractItem extracted = item.extract(getApplicationContext());
        changeFragment(extracted);
    }

    /**
     * Updates... the action bar!
     * @param item Item with new info for the actionbar.
     */
    private void updateActionBar(AbstractItem item) {
        getSupportActionBar().setTitle(item.getName());
        getSupportActionBar().setSubtitle(item.getPath());
    }

}
