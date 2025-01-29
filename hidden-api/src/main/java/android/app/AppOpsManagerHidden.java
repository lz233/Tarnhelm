package android.app;

import android.content.Context;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(AppOpsManager.class)
public class AppOpsManagerHidden {

    public void startWatchingNoted(int[] ops, OnOpNotedListener listener) {
        throw new RuntimeException("Stub!");
    }

    public void stopWatchingNoted(OnOpNotedListener listener) {
        throw new RuntimeException("Stub!");
    }

    public void setMode(String op, int uid, String packageName, int mode) {
        throw new RuntimeException("Stub!");
    }

    public interface OnOpNotedListener {
        /**
         * Called when an app-op is noted.
         *
         * <p> Implement this method and not
         * {@link #onOpNoted(String, int, String, String, int, int, int)} if callbacks are
         * required only on op notes for the default device {@link Context#DEVICE_ID_DEFAULT}.
         *
         * @param op             The operation that was noted.
         * @param uid            The UID performing the operation.
         * @param packageName    The package performing the operation.
         * @param attributionTag The attribution tag performing the operation.
         * @param flags          The flags of this op
         * @param result         The result of the note.
         */
        void onOpNoted(String op, int uid, String packageName, String attributionTag, int flags, int result);

        /**
         * Similar to {@link #onOpNoted(String, int, String, String, int, int, int)},
         * but also includes the virtual device id of the op is now active or inactive.
         *
         * <p> Implement this method if callbacks are required for op notes on all devices.
         * If not implemented explicitly, the default implementation will notify for the
         * default device {@link Context#DEVICE_ID_DEFAULT} only.
         *
         * <p> If implemented, {@link #onOpNoted(String, int, String, String, int, int)}
         * will not be called automatically.
         *
         * @param op              The operation that was noted.
         * @param uid             The UID performing the operation.
         * @param packageName     The package performing the operation.
         * @param attributionTag  The attribution tag performing the operation.
         * @param virtualDeviceId the device that noted the operation
         * @param flags           The flags of this op
         * @param result          The result of the note.
         */
        default void onOpNoted(String op, int uid, String packageName, String attributionTag, int virtualDeviceId, int flags, int result) {
            if (virtualDeviceId == Context.DEVICE_ID_DEFAULT) {
                onOpNoted(op, uid, packageName, attributionTag, flags, result);
            }
        }
    }
}
