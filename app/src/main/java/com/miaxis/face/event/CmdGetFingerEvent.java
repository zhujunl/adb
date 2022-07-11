package com.miaxis.face.event;

/**
 * Created by xu.nan on 2018/3/23.
 */

public class CmdGetFingerEvent {

    private String finger64;

    public CmdGetFingerEvent() {
    }

    public CmdGetFingerEvent(String finger64) {
        this.finger64 = finger64;
    }

    public String getFinger64() {
        return finger64;
    }

    public void setFinger64(String finger64) {
        this.finger64 = finger64;
    }
}
