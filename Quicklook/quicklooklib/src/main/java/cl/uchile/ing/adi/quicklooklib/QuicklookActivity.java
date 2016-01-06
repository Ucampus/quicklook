package cl.uchile.ing.adi.quicklooklib;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cl.uchile.ing.adi.quicklooklib.fragments.FileItemFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.FileItem;
import cl.uchile.ing.adi.quicklooklib.fragments.FragmentManager;

public class QuicklookActivity extends AppCompatActivity implements FileItemFragment.OnListFragmentInteractionListener {
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quicklook);

        this.url = getIntent().getStringExtra("url");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url.replace("https", "http")));
                startActivity(i);
            }
        });

        changeFragment(this.url,false);
    }

    public void changeFragment(String url, boolean backstack){
        Fragment fragment = FragmentManager.newInstance(url);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.quicklook_fragment, fragment, "QuickLook");
        if (backstack) t.addToBackStack(null);
        t.commit();
    }

    public void changeFragment(String url) {
        changeFragment(url, true);
    }

    public void onListFragmentInteraction(FileItem f) {
        changeFragment(f.getPath());

    }

    public void onListFragmentCreation(FileItem f) {
        updateActionBar(f);
    }

    private void updateActionBar(FileItem f) {
        getSupportActionBar().setTitle(f.getName());
        getSupportActionBar().setSubtitle(f.getPath());
    }

}
