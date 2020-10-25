package net.sleepinginthevoid.horoscope;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    PackageManager packageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Get list of installed apps with permissions
        packageManager = getPackageManager();
        List<PackageInfo> allPackageList = packageManager.getInstalledPackages(
                PackageManager.GET_PERMISSIONS);
        new AppData(this, packageManager, allPackageList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}