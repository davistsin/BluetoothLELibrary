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

package com.qindachang.bluetoothle;

import android.util.Log;

/**
 * Created on 2017/1/9.
 *
 * @author Qin DaChang
 * @see <a href="https://github.com/qindachang">https://github.com/qindachang</a>
 */

/* package */ class BleLogger {

    public static void v(boolean enable, final String tag, final String text) {
        if (enable)
            Log.v(tag, text);
    }

    public static void d(boolean enable, final String tag, final String text) {
        if (enable) {
            Log.d(tag, text);
        }
    }

    public static void i(boolean enable, final String tag, final String text) {
        if (enable)
            Log.i(tag, text);
    }

    public static void w(boolean enable, final String tag, final String text) {
        if (enable) {
            Log.w(tag, text);
        }
    }

    public static void e(boolean enable, final String tag, final String text) {
        if (enable)
            Log.e(tag, text);
    }

    public static void e(boolean enable, final String tag, final String text, final Throwable e) {
        if (enable)
            Log.e(tag, text, e);
    }

    public static void wtf(boolean enable, final String tag, final String text) {
        if (enable) {
            Log.wtf(tag, text);
        }
    }

    public static void wtf(boolean enable, final String tag, final String text, final Throwable e) {
        if (enable) {
            Log.wtf(tag, text, e);
        }
    }
}
