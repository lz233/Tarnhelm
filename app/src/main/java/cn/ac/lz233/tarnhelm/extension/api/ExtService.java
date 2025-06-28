package cn.ac.lz233.tarnhelm.extension.api;

import android.content.Context;
import android.view.View;

public abstract class ExtService {

    private ExtService() {}

    /**
     * This constructor will be called by the extension framework.
     * Never call this constructor by yourself.
     *
     * @param extContext The context of the extension.
     */
    public ExtService(ExtContext extContext) {}

    /**
     * This method will be called when the extension is installed.
     */
    public abstract void onExtInstall();

    /**
     * This method will be called when the extension is uninstalled.
     */
    public abstract void onExtUninstall();

    /**
     * This method will be called when user click the check update button of the extension.
     *
     * @return The direct update link of the extension.
     */
    public abstract String onCheckUpdate();

    /**
     * This method should handle the charSequence and return a processed string.
     *
     * @param charSequence The charSequence to be handled by the extension.
     */
    public abstract String onHandleString(CharSequence charSequence);

    /**
     * This method should build the configuration panel of the extension by returning a view which may contains widgets that are wrapped in a Layout.
     */
    public abstract View onRequestConfigurationPanel(Context context);
}
