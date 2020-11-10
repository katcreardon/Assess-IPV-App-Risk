package apprisk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppRisk {
	File whitelistTxt = new File("src/res/whitelist.txt");
	File permWeightFile = new File("src/res/permissionWeights.txt");
//	File appDataFile = new File("src/res/appdata.txt");					// Comment for testing
//	File appDataFile = new File("src/res/test_appdata.txt");     // Uncomment for testing
	File appDataFile = new File("src/res/seed_appdata.txt");		// Uncomment for testing
	Map<String, Permissions> permWeightMap = new HashMap<>();
	ArrayList<App> appList = new ArrayList<>();
	ArrayList<String> whiteList = new ArrayList();
	String[] listData;

	public AppRisk() {
		try {
			getWeights(permWeightMap);
			appList = readAppData();
			whiteList = readWhitelist(whitelistTxt);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*  Lines 1 - 18: dangerous shared with spyware counts
        Lines 19 - 24: dangerous spyware only
        Lines 25 - 26: dangerous popular only
        Lines 27 - 29: signature shared
        Lines 30 - 49: signature spyware only
        Lines 50 - 51: signature popular only
        Lines 52 - 57: removed shared
        Lines 58 - 62: removed spyware
        Lines 63 - 64: removed popular
	 */
	private void getWeights(Map<String, Permissions> map) throws IOException {		
		BufferedReader br = new BufferedReader(new FileReader(permWeightFile));
		String line;
		String[] result;

		// Very Dangerous
		if ((br.readLine()).equals("Very Dangerous")) {
			while (!(line = br.readLine()).equals("")) {
				result = line.split("\t");
				double weight = 3 + Double.parseDouble(result[2]);
				// Adds "permName: type, weight"
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		// Moderately Dangerous
		if ((br.readLine()).equals("Moderately Dangerous")) {
			while (!(line = br.readLine()).equals("")) {
				result = line.split("\t");
				double weight = 2 + Double.parseDouble(result[2]);
				// Adds "permName: type, weight"
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		// Less Dangerous
		if ((br.readLine()).equals("Less Dangerous")) {
			while (!(line = br.readLine()).equals("")) {
				result = line.split("\t");
				double weight = 1 + Double.parseDouble(result[2]);
				// Adds "permName: type, weight"
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		// Only Spyware Dangerous
		if ((br.readLine()).equals("Only Spyware Dangerous")) {
			while (!(line = br.readLine()).equals("")) {
				result = line.split("\t");
				double weight = 1.5 + Double.parseDouble(result[2]);
				// Adds "permName: type, weight"
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		// Only Popular Dangerous
		if ((br.readLine()).equals("Only Popular Dangerous")) {
			while (!(line = br.readLine()).equals("")) {
				result = line.split("\t");
				double weight = 1;
				// Adds "permName: type, weight"
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		// Shared Signature
		if ((br.readLine()).equals("Shared Signature")) {
			while (!(line = br.readLine()).equals("")) {
				result = line.split("\t");
				double weight = 1 + Double.parseDouble(result[2]);;
				// Adds "permName: type, weight"
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		// Only Spyware Signature
		if ((br.readLine()).equals("Only Spyware Signature")) {
			while (!(line = br.readLine()).equals("")) {
				result = line.split("\t");
				double weight = 3 + Double.parseDouble(result[2]);
				// Adds "permName: type, weight"
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		// Only Popular Signature
		if ((br.readLine()).equals("Only Popular Signature")) {
			while (!(line = br.readLine()).equals("")) {
				result = line.split("\t");
				double weight = 1;
				// Adds "permName: type, weight"
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		// Removed
		if ((br.readLine()).equals("Removed")) {
			while ((line = br.readLine()) != null) {
				result = line.split("\t");
				double weight = 0.3;
				// Adds "permName: type, weight"
				map.put(result[1], new Permissions(result[0], weight));
			}
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
		int weightPermCount = 0;
		int totalPermCount;
		double risk = 0;
		double ratio = 0;
		String perms = "";
		
		for (App a : appList) {
			if (a.getAppName().equals(selectedApp)) {
				System.out.print(selectedApp + " ");
				firstInstall = a.getFirstInstall();
				lastMod = a.getLastMod();
				totalPermCount = a.getPerms().size();
				for (String s : a.getPerms()) {
					if (permWeightMap.containsKey(s)) {
						risk += permWeightMap.get(s).getWeight();
						perms += s + "  "+ String.format("%.2f", permWeightMap.get(s).getWeight()) + "\n";
						weightPermCount += 1;
					}
				}
				ratio = (double)weightPermCount / totalPermCount;
				System.out.println(weightPermCount + "/" + totalPermCount + " = " + String.format("%.2f", ratio));
			}
		}
		if (ratio >= 0.4 && ratio < 1) {
			risk *= 1 + ratio;
		}
		String[] data = {String.format("%.2f", risk), firstInstall, lastMod, perms};
		return data;
	}
	
	ArrayList<String> readWhitelist(File wl) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(wl));
		String line;
		String[] result;
		ArrayList<String> list = new ArrayList<>();
		
		while ((line = br.readLine()) != null) {
			result = line.split(",");
			list.add(result[0]);
		}
		
		return list;
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
	
	public ArrayList<String> getWhiteList() {
		return whiteList;
	}
}
