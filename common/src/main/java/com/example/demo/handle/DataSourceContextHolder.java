package com.example.demo.handle;

public class DataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void set(String dataSource) {
        CONTEXT_HOLDER.set(dataSource);
    }

    public static String get() {
        return CONTEXT_HOLDER.get();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}