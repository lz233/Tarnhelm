package cn.ac.lz233.tarnhelm.extension.api;

public abstract class ExtService {

    private ExtService() {}

    public ExtService(ExtContext extContext) {}

    public abstract void onExtInstall();

    public abstract String handleLoadString(CharSequence charSequence);

    public abstract void onExtUninstall();

    public abstract String checkUpdate();

}
