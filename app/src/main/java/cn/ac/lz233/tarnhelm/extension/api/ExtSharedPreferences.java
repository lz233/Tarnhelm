package cn.ac.lz233.tarnhelm.extension.api;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface ExtSharedPreferences {

    Map<String, ?> getAll();

    @Nullable
    String getString(String var1, @Nullable String var2);

    @Nullable
    Set<String> getStringSet(String var1, @Nullable Set<String> var2);

    int getInt(String var1, int var2);

    long getLong(String var1, long var2);

    float getFloat(String var1, float var2);

    boolean getBoolean(String var1, boolean var2);

    boolean contains(String var1);

    Editor edit();

    interface Editor {
        Editor putString(String var1, @Nullable String var2);

        Editor putStringSet(String var1, @Nullable Set<String> var2);

        Editor putInt(String var1, int var2);

        Editor putLong(String var1, long var2);

        Editor putFloat(String var1, float var2);

        Editor putBoolean(String var1, boolean var2);

        Editor remove(String var1);

        Editor clear();

        boolean commit();

        void apply();
    }

}
