package com.davistsin.bluetoothlelibrary.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;


public class LocationUtil {

    private LocationUtil() {
        //no instance
    }

    /**
     * 判断是否启动定位服务
     *
     * @param context context
     * @return 是否启动定位服务
     */
    public static boolean isOpenLocService(final Context context) {
        boolean isGps = false; //判断GPS定位是否启动
        boolean isNetwork = false; //判断网络定位是否启动
        if (context != null) {
            LocationManager locationManager
                    = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                //通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
                isGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                //通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
                isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
            if (isGps || isNetwork) {
                return true;
            }
        }
        return false;
    }

    /**
     * 跳转定位服务界面
     *
     * @param activity activity
     */
    public static void gotoLocServiceSettings(Activity activity) {
        final Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    /**
     * 跳转定位服务界面
     *
     * @param activity    activity
     * @param requestCode requestCode
     */
    public static void gotoLocServiceSettings(Activity activity, int requestCode) {
        final Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(intent, requestCode);
    }
}
