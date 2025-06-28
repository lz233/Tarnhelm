package cn.ac.lz233.tarnhelm.extension.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ITarnhelmExt {

    ExtInfo extensionInfo();
    ExtService createExtensionService(ExtContext extContext);

    interface ExtInfo {

        @NotNull
        String id();

        @Nullable
        String author();

        @NotNull
        String name();

        @Nullable
        String description();

        @Nullable
        String extensionURL();

        int versionCode();

        @Nullable
        String versionName();

        boolean hasConfigurationPanel();

        int minTarnhelmSdkVersion();

        int minAndroidSdkVersion();

        @NotNull
        String[] regexes();

    }

}
