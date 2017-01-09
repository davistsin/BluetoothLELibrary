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


/**
 * Created on 2017/1/9.
 *
 * @author Qin DaChang
 * @see <a href="https://github.com/qindachang">https://github.com/qindachang</a>
 */

public class ScanBleException extends BleException {

    public ScanBleException(int status, int type) {
        this(status, type, "");
        String detailMessage;
        if (status == 1) {
            detailMessage = "Fails to start scan as BLE scan with the same settings is already started by the app.";
        } else if (status == 2) {
            detailMessage = "Fails to start scan as app cannot be registered.";
        } else if (status == 3) {
            detailMessage = "Fails to start scan due an internal error";
        } else if (status == 4) {
            detailMessage = "Fails to start power optimized scan as this feature is not supported.";
        } else if (status == 5) {
            detailMessage = "Fails to start scan as it is out of hardware resources.";
        } else {
            detailMessage = "I don't know..";
        }
        setDetailMessage(detailMessage);
    }

    public ScanBleException(int status, int type, String detailMessage) {
        super(status, type, detailMessage);
    }

    @Override
    public String toString() {
        return "BleException(BLE) : " + "\n" +
                "{ " + "\n" +
                "errorCode = " + getStatus() + ",\n" +
                "type = " + getTypeArr()[getType()] + ",\n" +
                "detail = " + getDetailMessage() + "\n" +
                "}";
    }
}
