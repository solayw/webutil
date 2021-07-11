package com.github.solayw.webutil;

public class R<T>
{

    public static String successMsg;
    public static int successCode;
    public final int code;

    public final String msg;

    public final T data;

    private R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static R ok() {
        return new R(successCode, successMsg, null);
    }

    public static <T> R<T> ok(T data) {
        return new R<T>(successCode, null, data);
    }

    public static R error(int code, String msg) {
        return new R(code, msg, null);
    }

    public static <T> R<T> error(int code, String msg, T data) {
        return new R<>(code, msg, data);
    }
}
