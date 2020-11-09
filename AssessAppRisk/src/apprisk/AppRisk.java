package apprisk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppRisk {
	File permWeightFile = new File("src/res/permissionWeights.txt");
	File appDataFile = new File("src/res/appdata.txt");
//	static File appDataFile = new File("src/res/test_appdata.txt");     // For testing
	Map<String, Permissions> permWeightMap = new HashMap<>();
	ArrayList<App> appList = new ArrayList<>();
	String[] listData;

	public AppRisk() {
		try {
			getWeights(permWeightMap);
			appList = readAppData();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		calculateRisk(appList);
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
	private void getWeights(Map<String, Permissions> map) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(permWeightFile));
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
		
		br.close();
	}

	// File must have two blank lines at end to work
	private ArrayList<App> readAppData() throws IOException {
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
				appName = (line.split(": "))[1];
			}
			if (line.contains("Package name:")) {
				packageName = (line.split(": "))[1];
			}
			if (line.contains("First installed:")) {
				firstInstall = (line.split(": "))[1];
			}
			if (line.contains("Last modified:")) {
				lastMod = (line.split(": "))[1];
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
		
		br.close();
		
		listData = new String[apps.size()];
		for (int i = 0; i < apps.size(); i++) {
			listData[i] = apps.get(i).getAppName() + " (" + apps.get(i).getPackageName() + ")";
		}
		
		return apps;
	}

	public String[] calculateRisk(String selectedApp) {
		String firstInstall = null;
		String lastMod = null;
		double risk = 0;
		String perms = "";
		
		for (App a : appList) {
			if (a.getAppName().equals(selectedApp)) {
				firstInstall = a.getFirstInstall();
				lastMod = a.getLastMod();
				for (String s : a.getPerms()) {
					if (permWeightMap.containsKey(s)) {
						risk += permWeightMap.get(s).getWeight();
						perms += s + "\n";
					}
				}
			}
		}
		String[] data = {String.format("%.2f", risk), firstInstall, lastMod, perms};
		return data;
	}
	
	public String[] getListData() {
		return listData;
	}
	
	public ArrayList<App> getAppList() {
		return appList;
	}
	
	public Map<String, Permissions> getPermWeightMap() {
		return permWeightMap;
	}
}
