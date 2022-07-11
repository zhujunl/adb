package com.miaxis.face.event;

import com.miaxis.face.bean.Record;

public class CmdIdCardDoneEvent {

    private Record record;

    public CmdIdCardDoneEvent(Record record) {
        this.record = record;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }
}
