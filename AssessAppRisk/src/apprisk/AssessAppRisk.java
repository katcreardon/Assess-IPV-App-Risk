package apprisk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AssessAppRisk {
    static File permWeightFile = new File("src/res/permissionWeights.txt");
    static File appDataFile = new File("src/res/appdata.txt");
    static Map<String, Permissions> permWeightMap = new HashMap<>();
    static ArrayList<App> appList = new ArrayList<>();

    public static void main(String[] args) {
        try {
            getWeights(permWeightMap);
            appList = readAppData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        calculateRisk(appList);
    }
    /*  Lines 1 - 17: dangerous shared spyware and popular with spyware counts
        Lines 18 - 23: dangerous spyware only
        Lines 24 - 26: dangerous popular only
        Lines 27 - 29: signature shared
        Lines 30 - 47: signature spyware only
        Lines 48 - 49: signature popular only
        Lines 50 - 55: removed shared
        Lines 56 - 59: removed spyware
        Lines 60 - 61: removed popular
     */
    static void getWeights(Map<String, Permissions> map) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(permWeightFile));
        String line;
        String[] result;

        for (int i = 0; i <= 16; i++) {
            result = br.readLine().split("\t");
            map.put(result[1], new Permissions(result[0], Double.parseDouble(result[2]) + 0.5));
        }
        for (int i = 17; i <= 22; i++) {
            result = br.readLine().split("\t");
            map.put(result[1], new Permissions(result[0], Double.parseDouble(result[2]) + 1.0));
        }
        for (int i = 23; i <= 25; i++) {
            result = br.readLine().split("\t");
            map.put(result[1], new Permissions(result[0], Double.parseDouble(result[2])));
        }
        for (int i = 26; i <= 28; i++) {
            result = br.readLine().split("\t");
            map.put(result[1], new Permissions(result[0], Double.parseDouble(result[2]) + 0.5));
        }
        for (int i = 29; i <= 46; i++) {
            result = br.readLine().split("\t");
            map.put(result[1], new Permissions(result[0], Double.parseDouble(result[2]) + 1.0));
        }
        for (int i = 47; i <= 48; i++) {
            result = br.readLine().split("\t");
            map.put(result[1], new Permissions(result[0], Double.parseDouble(result[2])));
        }
        for (int i = 49; i <= 60; i++) {
            result = br.readLine().split("\t");
            map.put(result[1], new Permissions(result[0], Double.parseDouble(result[2]) + 0.3));
        }
    }

    // File must have two blank lines at end to work
    static ArrayList<App> readAppData() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(appDataFile));
        String line;
        String appName = null;
        String packageName = null;
        String firstInstall = null;
        String lastMod = null;
        ArrayList<String> perms;
        ArrayList<App> apps = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            perms = new ArrayList<>();
            if (line.contains("App name:")) {
                appName = line;
            }
            if (line.contains("Package name:")) {
                packageName = line;
            }
            if (line.contains("First installed:")) {
                firstInstall = line;
            }
            if (line.contains("Last modified:")) {
                lastMod = line;
            }
            if (line.contains("Permissions:")) {
                while (!(line = br.readLine()).equals("")) {
                    perms.add(line);
                }
            }
            if (line.equals("")) {
                apps.add(new App(appName, packageName, firstInstall, lastMod, perms));
            }
        }
        return apps;
    }

    static void calculateRisk(ArrayList<App> list) {
        for (App a : list) {
            double risk = 0;
            System.out.println(a.getAppName());
            System.out.println(a.getPackageName());
            System.out.println(a.getFirstInstall());
            System.out.println(a.getLastMod());
            for (String s : a.getPerms()) {
                if (permWeightMap.containsKey(s)) {
                    System.out.println(s + " " + permWeightMap.get(s).getWeight());
                    risk += permWeightMap.get(s).getWeight();
                }
            }
            System.out.printf("Risk = %.2f\n", risk);
            System.out.println();
        }
    }
}
