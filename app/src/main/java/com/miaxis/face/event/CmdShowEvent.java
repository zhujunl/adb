package com.miaxis.face.event;

/**
 * @author ZJL
 * @date 2022/5/26 13:24
 * @des
 * @updateAuthor
 * @updateDes
 */
public class CmdShowEvent {
    private String data;

    public CmdShowEvent(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
