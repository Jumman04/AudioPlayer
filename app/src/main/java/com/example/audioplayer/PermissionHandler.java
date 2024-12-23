package com.example.audioplayer;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHandler {

    private final Activity mActivity;
    private String[] permissions;

    // Constructor initializing the activity
    public PermissionHandler(Activity activity) {
        this.mActivity = activity;
    }

    // Method to check if storage permission is granted
    public boolean checkStoragePermission() {
        // Check if the device is running on Android 13 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Declare permissions for Android 13+
            permissions = new String[]{Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            // Permissions for Android 6 to 12 (minSdkVersion >= 24)
            permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        // Check if permissions are granted
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    // Method to request permissions
    public void requestPermissions() {
        boolean shouldShowRationale = false;
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                shouldShowRationale = true;
                break;
            }
        }

        if (shouldShowRationale) {
            showRationaleDialog(permissions);
        } else {
            ActivityCompat.requestPermissions(mActivity, permissions, 1);
        }
    }

    // Method for check shouldShowRequestPermissionRationale, otherwise redirects to app settings.
    public void showDialog(final String[] permissions) {
        boolean shouldShowRationale = false;
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                shouldShowRationale = true;
                break;
            }
        }

        if (shouldShowRationale) {
            showRationaleDialog(permissions);
        } else {
            goToSettings();
        }
    }

    // Method for show rationale dialog when permission denied
    private void showRationaleDialog(final String[] permissions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Permission Required").setCancelable(false).setMessage("This app needs storage permissions to function properly. Please grant all of them.").setPositiveButton("OKAY", (dialog, which) -> ActivityCompat.requestPermissions(mActivity, permissions, 1)).setNegativeButton("NO THANKS", null).show();
    }

    // Method for navigate to app settings
    private void goToSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Permission Required").setCancelable(false).setMessage("Permission was denied and cannot be asked again. Please allow permission from app settings.").setPositiveButton("GO TO SETTINGS", (dialog, id) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", mActivity.getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivity(intent);
        }).setNegativeButton("NO THANKS", null).show();
    }
}