package com.miaxis.image;

/**
 * 通用Jni返回结果
 * <p>
 * code : 0 success ,otherwise fail
 * <p>
 * data : 返回结果，可能为空
 * <p>
 * msg  : 错误信息，可能为空
 */
public class Result<T> {
    public final int code;
    public final T data;
    public final String msg;

    private Result(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static <R> Result<R> success(R data) {
        return new Result<>(0, data, null);
    }

    public static <R> Result<R> error(int code) {
        return new Result<>(code, null, null);
    }

    public  boolean isSuccess() {
        return code == 0;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}';
    }
}
