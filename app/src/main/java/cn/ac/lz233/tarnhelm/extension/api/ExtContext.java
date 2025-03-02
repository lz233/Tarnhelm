package cn.ac.lz233.tarnhelm.extension.api;

import android.os.Build;

public interface ExtContext {

    int tarnhelmSdkVersion();

    default int androidSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    ExtSharedPreferences getSharedPreferences();

}
