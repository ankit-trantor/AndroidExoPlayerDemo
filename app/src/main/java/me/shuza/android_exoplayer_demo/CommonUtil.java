package me.shuza.android_exoplayer_demo;

import android.net.Uri;

/**
 * :=  created by:  Shuza
 * :=  create date:  11/20/2017
 * :=  (C) CopyRight Shuza
 * :=  www.shuza.me
 * :=  shuza.sa@gmail.com
 * :=  Fun  :  Coffee  :  Code
 **/

public class CommonUtil {
    //public static final String VIDEO_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    public static final String VIDEO_URL = "http://192.168.36.139/videos/test.mp4";

    public static Uri getVideoUri() {
        return Uri.parse(VIDEO_URL);
    }
}
