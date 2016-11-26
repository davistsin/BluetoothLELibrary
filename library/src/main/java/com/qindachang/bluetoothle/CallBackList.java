package com.qindachang.bluetoothle;

/**
 * Created on 2016/11/26.
 *
 * @author Qin DaChang
 * @see <a href="https://github.com/qindachang">https://github.com/qindachang</a>
 */

class CallBackList {

    enum Type {
        SCAN,
        CONNECT,
        WRITE,
        READ,
        NOTIFICATION
    }

    Type mType;

    private Object mTag;



    public void setTag(Object tag) {
        mTag = tag;
    }

    public Object getTag() {
        return mTag;
    }


}
