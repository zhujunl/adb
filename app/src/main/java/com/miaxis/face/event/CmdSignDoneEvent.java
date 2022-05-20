package com.miaxis.face.event;

/**
 * @author ZJL
 * @date 2022/5/19 11:21
 * @des
 * @updateAuthor
 * @updateDes
 */
public class CmdSignDoneEvent {
    private String base64;

    public CmdSignDoneEvent(String base64) {
        this.base64 = base64;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
}
