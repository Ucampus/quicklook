package cl.uchile.ing.adi.quicklooklib;


import android.app.Activity;

public interface QuicklookConfigurator {
    /**
     * Called when the activity is executing the onResume() lifecycle method.
     * You MUST implement this method to register any custom BroadcastReceiver you need.
     * @param activity the current Active activity
     */
    void registerBroadcasts(Activity activity);

    /**
     * Called when the activity is executing the onPause() lifecycle method.
     * You MUST implement this method to register any custom BroadcastReceiver you need.
     * @param activity the current Active activity
     */
    void unregisterBroadcasts(Activity activity);
}
