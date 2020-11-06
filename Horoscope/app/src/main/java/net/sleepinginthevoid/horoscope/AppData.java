package net.sleepinginthevoid.horoscope;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppData {
    Activity activity;
    PackageManager packageManager;
    List<PackageInfo> packageList;
    PackageInfo packageInfo;
    String thisPackage;
    String appLabel;
    String packageName;
    String permissions;
    String installed;
    String lastModified;

    public AppData(MainActivity main, PackageManager pm, List<PackageInfo> pl) {
        thisPackage = "net.sleepinginthevoid.horoscope";
        activity = main;
        packageManager = pm;
        List<PackageInfo> allPackageList = pl;
        packageList = new ArrayList<PackageInfo>();

        // To filter out system apps
        for (PackageInfo pi : allPackageList) {
            boolean b = isSystemPackage(pi);
            if (pi.packageName.equals(thisPackage)) {   // Ignore the Horoscope app
                b = true;
            }
            if(!b) {
                packageList.add(pi);
            }
        }
        // Get package information
        for (int i = 0; i < packageList.size(); i++) {
            packageInfo = packageList.get(i);
            appLabel = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
            packageName = packageInfo.packageName;
            installed = setDateFormat(packageInfo.firstInstallTime);
            lastModified = setDateFormat(packageInfo.lastUpdateTime);

            if (packageInfo.requestedPermissions != null) {
                permissions = getPermissions(packageInfo.requestedPermissions);
            } else {
                permissions = "none";
            }
            // Writing a file to Device File Explorer > data/data/net.sleepinginthevoid.horoscope
            try {
                FileOutputStream fOut = activity.openFileOutput("appdata.txt",
                        Context.MODE_APPEND);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                // Write the strings to the file
                osw.append("App name: " + appLabel + "\n");
                osw.append("Package name: " + packageName + "\n");
                osw.append("First installed: " + installed + "\n");
                osw.append("Last modified: " + lastModified + "\n");
                osw.append("Permissions:" + "\n" + permissions  + "\n");
                osw.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    // Return whether the given PackageInfo is a system package
    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    private String setDateFormat(long time) {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy, h:mm a");
        String strDate = formatter.format(date);
        return strDate;
    }

    private String getPermissions(String[] requestedPermissions) {
        String permission = "";
        for (int i = 0; i < requestedPermissions.length; i++) {
            permission = permission + requestedPermissions[i] + "\n";
        }
        return permission;
    }
}
