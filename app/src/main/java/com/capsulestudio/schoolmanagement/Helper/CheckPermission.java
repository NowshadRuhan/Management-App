package com.capsulestudio.schoolmanagement.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Juman on 12/16/2017.
 */

public class CheckPermission {
    Context context;
    public static final int RequestPermissionCode = 1;
    public CheckPermission(Context context) {
        this.context = context;

    }

    public boolean checkPermission() {

        int SecondPermissionResult = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);

        return SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission()
    {
        ActivityCompat.requestPermissions((Activity) context, new String[]
                {
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE

                }, RequestPermissionCode);
    }



    //  check single Permission
    public Boolean checkSinglePermission(String permissionName){

        int  PermissionOk = ContextCompat.checkSelfPermission(context, permissionName);
        return  PermissionOk == PackageManager.PERMISSION_GRANTED;
    }

    // request for single permission
    public void requestForSinglePermission(String requestName){
        ActivityCompat.requestPermissions( (Activity) context, new String[]
                {requestName}, RequestPermissionCode);
    }
}
