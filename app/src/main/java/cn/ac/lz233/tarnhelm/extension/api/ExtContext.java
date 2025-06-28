package cn.ac.lz233.tarnhelm.extension.api;

import android.os.Build;

public interface ExtContext {

    int tarnhelmSdkVersion();

    default int androidSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * Get the preferences storage of the extension.
     *
     * @return ExtSharedPreferences is nearly the same as the shared preferences in Android SDK.
     */
    ExtSharedPreferences getSharedPreferences();

}
