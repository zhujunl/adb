package com.miaxis.face.event;

/**
 * Created by xu.nan on 2018/3/28.
 */

public class CmdGetFingerDoneEvent {
    private String finger64;

    public CmdGetFingerDoneEvent() {
    }

    public CmdGetFingerDoneEvent(String finger64) {
        this.finger64 = finger64;
    }

    public String getFinger64() {
        return finger64;
    }

    public void setFinger64(String finger64) {
        this.finger64 = finger64;
    }
}
