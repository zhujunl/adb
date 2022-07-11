package com.miaxis.face.event;

/**
 * @author ZJL
 * @date 2022/5/19 11:30
 * @des
 * @updateAuthor
 * @updateDes
 */
public class CmdScanDoneEvent {
    private String content;

    public CmdScanDoneEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
