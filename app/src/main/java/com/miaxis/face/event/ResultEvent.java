package com.miaxis.face.event;

import com.miaxis.face.bean.Record;

import org.zz.api.MXFaceInfoEx;


/**
 * Created by Administrator on 2017/5/22 0022.
 */

public class ResultEvent {

    public static final int FACE_SUCCESS   = 0;
    public static final int FACE_FAIL      = 1;
    public static final int FINGER_SUCCESS = 2;
    public static final int FINGER_FAIL    = 3;
    public static final int VERIFY_FINGER  = 4;
    public static final int ID_PHOTO       = 5;
    public static final int WHITE_LIST_FAIL = 6;        // 白名单校验失败  不在白名单内
    public static final int BLACK_LIST_FAIL = 7;        // 黑名单校验失败  在黑名单内
    public static final int VALIDATE_FAIL   = 8;

    private int result;
    private Record record;
    private MXFaceInfoEx faceInfo;

    public ResultEvent(int result, Record record) {
        this.result = result;
        this.record = record;
    }

    public ResultEvent(int result, Record record, MXFaceInfoEx faceInfo) {
        this.result = result;
        this.record = record;
        this.faceInfo = faceInfo;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public MXFaceInfoEx getFaceInfo() {
        return faceInfo;
    }

    public void setFaceInfo(MXFaceInfoEx faceInfo) {
        this.faceInfo = faceInfo;
    }
}
