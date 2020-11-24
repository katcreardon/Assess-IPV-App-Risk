package apprisk;

import java.util.ArrayList;

public class App {
    private final String appName;
    private final String packageName;
    private final String firstInstall;
    private final String lastMod;
    private final ArrayList<String> perms;

    public App(String aN, String pN, String fI, String lM, ArrayList<String> p) {
        this.appName = aN;
        this.packageName = pN;
        this.firstInstall = fI;
        this.lastMod = lM;
        this.perms = p;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getFirstInstall() {
        return firstInstall;
    }

    public String getLastMod() {
        return lastMod;
    }

    public ArrayList<String> getPerms() {
        return perms;
    }
}
