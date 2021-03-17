package com.miaxis.face.event;

/**
 * Created by xu.nan on 2017/11/9.
 */

public class CmdShutterPhotoEvent {
    String photo64;

    public CmdShutterPhotoEvent(String photo64) {
        this.photo64 = photo64;
    }

    public CmdShutterPhotoEvent() {
    }

    public String getPhoto64() {
        return photo64;
    }

    public void setPhoto64(String photo64) {
        this.photo64 = photo64;
    }
}
