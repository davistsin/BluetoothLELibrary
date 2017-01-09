/*
 * Copyright (c) 2017, Qin Dachang
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qindachang.bluetoothle.exception;

import java.io.Serializable;

/**
 * Created on 2017/1/9.
 *
 * @author Qin DaChang
 * @see <a href="https://github.com/qindachang">https://github.com/qindachang</a>
 */

@SuppressWarnings("serial")
public class BleException implements Serializable {

    public static final int SCAN = 0;
    public static final int CONNECT = 1;
    public static final int WRITE_CHARACTERISTIC = 2;
    public static final int READ_CHARACTERISTIC = 3;
    public static final int READ_RSSI = 4;
    public static final int NOTIFICATION = 5;
    public static final int INDICATION = 6;

    private String[] typeArr = {"SCAN", "CONNECT", "WRITE_CHARACTERISTIC", "READ_CHARACTERISTIC",
            "READ_RSSI", "NOTIFICATION", "INDICATION"};

    private int status;
    private int type;
    private String detailMessage;

    public BleException(int status, int type, String detailMessage) {
        this.status = status;
        this.type = type;
        this.detailMessage = detailMessage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public BleException setType(int type) {
        this.type = type;
        return this;
    }

    public String[] getTypeArr() {
        return typeArr;
    }

    public void setTypeArr(String[] typeArr) {
        this.typeArr = typeArr;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public BleException setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }


    @Override
    public String toString() {
        return "BleException : " + "\n"+
                "{ " + "\n"+
                "status = "+ status + ",\n"+
                "type = "+ typeArr[type] + ",\n"+
                "detail = "+detailMessage+ "\n"+
                "}";
    }
}
