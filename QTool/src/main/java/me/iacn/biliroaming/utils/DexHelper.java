package me.iacn.biliroaming.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;

public class DexHelper implements AutoCloseable, Closeable {
    public static final int NO_CLASS_INDEX = -1;
    private final ClassLoader classLoader;
    private final long token;

    public DexHelper(@NonNull ClassLoader classLoader) {
        this.classLoader = classLoader;
        token = load(classLoader);
    }

    @NonNull
    public native long[] findMethodUsingString(
            @NonNull String str, boolean matchPrefix, long returnType, short parameterCount,
            @Nullable String parameterShorty, long declaringClass, @Nullable long[] parameterTypes,
            @Nullable long[] containsParameterTypes, @Nullable int[] dexPriority, boolean findFirst);
    @NonNull
    public native long[] findMethodInvoking(long methodIndex, long returnType,
                                            short parameterCount, @Nullable String parameterShorty,
                                            long declaringClass, @Nullable long[] parameterTypes,
                                            @Nullable long[] containsParameterTypes,
                                            @Nullable int[] dexPriority, boolean findFirst);

    @NonNull
    public native long[] findMethodInvoked(long methodIndex, long returnType,
                                           short parameterCount, @Nullable String parameterShorty,
                                           long declaringClass, @Nullable long[] parameterTypes,
                                           @Nullable long[] containsParameterTypes,
                                           @Nullable int[] dexPriority, boolean findFirst);
    @Nullable
    public native Member decodeMethodIndex(long methodIndex);

    public native long encodeMethodIndex(@NonNull Member method);

    public native long encodeClassIndex(@NonNull Class<?> clazz);

    @Override
    public native void close();

    @Override
    protected void finalize() {
        close();
    }

    private native long load(@NonNull ClassLoader classLoader);
}

