package com.miaxis.face.event;

import android.graphics.Bitmap;

import com.miaxis.face.bean.Record;

/**
 * @author ZJL
 * @date 2022/5/13 19:57
 * @des
 * @updateAuthor
 * @updateDes
 */
public class ReadCardEvent {

    private Record mRecord;
    private Bitmap face;
    private int verifyMode;

    public ReadCardEvent(Record record, Bitmap face, int verifyMode) {
        mRecord = record;
        this.face = face;
        this.verifyMode = verifyMode;
    }

    public Record getRecord() {
        return mRecord;
    }

    public void setRecord(Record record) {
        mRecord = record;
    }

    public Bitmap getFace() {
        return face;
    }

    public void setFace(Bitmap face) {
        this.face = face;
    }

    public int getVerifyMode() {
        return verifyMode;
    }

    public void setVerifyMode(int verifyMode) {
        this.verifyMode = verifyMode;
    }

    @Override
    public String toString() {
        return "ReadCardEvent{" +
                "mRecord=" + mRecord +
                ", face=" + face +
                ", verifyMode=" + verifyMode +
                '}';
    }
}
