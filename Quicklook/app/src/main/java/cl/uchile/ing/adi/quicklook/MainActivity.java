package cl.uchile.ing.adi.quicklook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import cl.uchile.ing.adi.quicklooklib.QuicklookActivity;

public class MainActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSimpleFile(View v){
        Log.d(this.getClass().getSimpleName(), "onSimpleFile");
        Intent i = new Intent(this, QuicklookActivity.class);
        i.putExtra("url", "http://www.u-cursos.cl");
        startActivity(i);
    }
}
