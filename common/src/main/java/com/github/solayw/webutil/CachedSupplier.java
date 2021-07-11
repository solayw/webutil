package com.github.solayw.webutil;

import java.util.function.Supplier;

public class CachedSupplier<T> implements Supplier<T>
{
    private Supplier<T> supplier;
    private T obj;
    private boolean cached;

    public CachedSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if(!cached) {
            obj = supplier.get();
            cached = true;
        }
        return obj;
    }
}
