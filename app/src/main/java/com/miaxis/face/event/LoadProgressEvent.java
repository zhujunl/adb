package com.miaxis.face.event;

/**
 * Created by xu.nan on 2018/3/27.
 */

public class LoadProgressEvent<T> {

    private int max;
    private int progress;
    private T item;

    public LoadProgressEvent(int max, int progress) {
        this.max = max;
        this.progress = progress;
    }

    public LoadProgressEvent() {
    }

    public LoadProgressEvent(int progress) {
        this.progress = progress;
    }

    public LoadProgressEvent(T item) {
        this.item = item;
    }

    public LoadProgressEvent(int max, int progress, T item) {
        this.max = max;
        this.progress = progress;
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
