package cl.uchile.ing.adi.quicklooklib.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.File;

/**
 * Clase que recibe una uri y crea un fragmento
 * para esa uri
 * Created by dudu on 04-01-2016.
 */
public class FragmentManager {


    public static Fragment newInstance(String path) {
        Fragment fragment = getFragment(path);
        Bundle args = new Bundle();
        args.putString(DefaultFragment.ARG_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    private static Fragment getFragment(String path) {
        if(new File(path).isDirectory()) {
            return new FileItemFragment();
        } else {
            if (path.endsWith(".jpg") || path.endsWith(".png")) {
                return new WebFragment();
            } else {
                return new DefaultFragment();
            }
        }
    }
}
