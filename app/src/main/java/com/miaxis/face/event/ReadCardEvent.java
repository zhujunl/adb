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

    public ReadCardEvent(Record record, Bitmap face) {
        mRecord = record;
        this.face = face;
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
}
