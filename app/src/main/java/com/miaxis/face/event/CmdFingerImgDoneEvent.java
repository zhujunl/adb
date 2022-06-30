package com.miaxis.face.event;

/**
 * @author ZJL
 * @date 2022/5/19 11:28
 * @des
 * @updateAuthor
 * @updateDes
 */
public class CmdFingerImgDoneEvent {
    private String base64;
    private String finger;

    public CmdFingerImgDoneEvent(String base64, String finger) {
        this.base64 = base64;
        this.finger = finger;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getFinger() {
        return finger;
    }

    public void setFinger(String finger) {
        this.finger = finger;
    }
}
