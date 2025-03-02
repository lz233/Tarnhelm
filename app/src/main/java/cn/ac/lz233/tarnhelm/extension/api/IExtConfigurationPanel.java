package cn.ac.lz233.tarnhelm.extension.api;

import android.content.Context;
import android.view.View;

public interface IExtConfigurationPanel {

    View onRequestConfigurationPanel(Context context, ExtSharedPreferences sharedPreferences);

}
