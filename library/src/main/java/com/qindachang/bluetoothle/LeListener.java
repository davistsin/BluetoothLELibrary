package com.qindachang.bluetoothle;

/**
 * Created on 2016/11/26.
 *
 * @author Qin DaChang
 * @see <a href="https://github.com/qindachang">https://github.com/qindachang</a>
 */

abstract class LeListener {
    private Object mTag;

    public Object getTag() {
        return mTag;
    }

    public void setTag(Object tag) {
        mTag = tag;
    }
}
