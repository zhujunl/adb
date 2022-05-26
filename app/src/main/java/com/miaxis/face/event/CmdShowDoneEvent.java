package com.miaxis.face.event;

/**
 * @author ZJL
 * @date 2022/5/26 13:53
 * @des
 * @updateAuthor
 * @updateDes
 */
public class CmdShowDoneEvent {
    private String state;
    private String message;

    public CmdShowDoneEvent(String state, String message) {
        this.state = state;
        this.message = message;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
