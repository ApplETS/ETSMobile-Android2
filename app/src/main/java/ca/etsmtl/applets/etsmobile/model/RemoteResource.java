package ca.etsmtl.applets.etsmobile.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Sonphil on 31-08-17.
 */

public class RemoteResource<T> {
    public static final int SUCCESS = 200;
    public static final int LOADING = 350;
    public static final int ERROR = 500;

    @NonNull
    public final int status;
    @Nullable
    public final T data;
    @Nullable
    public final String message;

    private RemoteResource(@NonNull int status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> RemoteResource<T> success(@NonNull T data) {
        return new RemoteResource<>(SUCCESS, data, null);
    }

    public static <T> RemoteResource<T> error(String msg, @Nullable T data) {
        return new RemoteResource<>(ERROR, data, msg);
    }

    public static <T> RemoteResource<T> loading(@Nullable T data) {
        return new RemoteResource<>(LOADING, data, null);
    }
}
