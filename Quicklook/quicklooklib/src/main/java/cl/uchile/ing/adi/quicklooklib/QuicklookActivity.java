package cl.uchile.ing.adi.quicklooklib;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cl.uchile.ing.adi.quicklooklib.fragments.DefaultFragment;

public class QuicklookActivity extends AppCompatActivity {
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quicklook);

        this.url = getIntent().getStringExtra("url");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url.replace("https", "http")));
                startActivity(i);
            }
        });

        changeFragment(this.url);
    }

    private void changeFragment(String url){
        Fragment fragment = fragmentForURL(url);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.quicklook_fragment, fragment, "QuickLook");
        t.commit();
    }

    /**
     * Returns the fragment which can handle the given URL.
     *
     * @param url
     * @return
     */
    private Fragment fragmentForURL(String url){
        return new DefaultFragment();
    }
}
