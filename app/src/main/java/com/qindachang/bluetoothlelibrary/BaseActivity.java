package com.qindachang.bluetoothlelibrary;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by qindachang on 2017/3/30.
 */

public class BaseActivity extends AppCompatActivity {

    String[] locations = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    protected boolean checkLocationPermission() {
        return EasyPermissions.hasPermissions(this, locations);
    }

    protected void requestLocationPermission() {
        EasyPermissions.requestPermissions(this, "Android 6.0以上扫描蓝牙需要该权限", Permission.LOCATION, locations);
    }
}
