package apprisk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AppRisk {
	File permWeightFile = new File("src/res/permissionWeights.txt");
	File whitelistTxt = new File("src/res/whitelist.txt");
	File permDescriptions = new File("src/res/permissionDescriptions.txt");
	File appDataFile = new File("src/res/appdata.txt");					// Comment for testing
//	File appDataFile = new File("src/res/max_appdata.txt");     // Uncomment for testing
//	File appDataFile = new File("src/res/seed_appdata.txt");		// Uncomment for testing
	Map<String, Permissions> permWeightMap = new HashMap<>();
	ArrayList<App> appList = new ArrayList<>();
	Map<String, String> whitelist = new HashMap<>();
	Map<String, String> permDesc = new HashMap<>();
	String[] listData;

	public AppRisk() {
		try {
			getWeights(permWeightMap);
			appList = readAppData();
			whitelist = readWhitelist(whitelistTxt);
			permDesc = readPermissionDescriptions(permDescriptions);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Reads permissions and usage rates from file and adds to map
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
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		// Less Dangerous
		if ((br.readLine()).equals("Less Dangerous")) {
			while (!(line = br.readLine()).equals("")) {
				result = line.split("\t");
				double weight = 1 + Double.parseDouble(result[2]);
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		// Only Spyware Dangerous
		if ((br.readLine()).equals("Only Spyware Dangerous")) {
			while (!(line = br.readLine()).equals("")) {
				result = line.split("\t");
				double weight = 1.5 + Double.parseDouble(result[2]);
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		// Only Popular Dangerous
		if ((br.readLine()).equals("Only Popular Dangerous")) {
			while (!(line = br.readLine()).equals("")) {
				result = line.split("\t");
				double weight = 1;
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		// Shared Signature
		if ((br.readLine()).equals("Shared Signature")) {
			while (!(line = br.readLine()).equals("")) {
				result = line.split("\t");
				double weight = 1 + Double.parseDouble(result[2]);
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		// Only Spyware Signature
		if ((br.readLine()).equals("Only Spyware Signature")) {
			while (!(line = br.readLine()).equals("")) {
				result = line.split("\t");
				double weight = 3 + Double.parseDouble(result[2]);
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		// Only Popular Signature
		if ((br.readLine()).equals("Only Popular Signature")) {
			while (!(line = br.readLine()).equals("")) {
				result = line.split("\t");
				double weight = 1;
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		// Removed
		if ((br.readLine()).equals("Removed")) {
			while ((line = br.readLine()) != null) {
				result = line.split("\t");
				double weight = 0.3;
				map.put(result[1], new Permissions(result[0], weight));
			}
		}
		
		br.close();
	}
	// Reads app data from file, adds names for list selection panel, and adds apps to list
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
	// Calculates risk of selected list item using weight map
	public String[] calculateRisk(String selectedApp) {
		String packageName = null;
		String firstInstall = null;
		String lastMod = null;
		int weightPermCount = 0;
		int totalPermCount;
		double risk = 0;
		double ratio = 0;
		List<String> permsList = new ArrayList<>();
		
		for (App a : appList) {
			if (a.getAppName().equals(selectedApp)) {
				packageName = a.getPackageName();
				firstInstall = a.getFirstInstall();
				lastMod = a.getLastMod();
				totalPermCount = a.getPerms().size();
				for (String s : a.getPerms()) {
					if (permWeightMap.containsKey(s)) {
						risk += permWeightMap.get(s).getWeight();
						permsList.add(permWeightMap.get(s).getType() + " " + s);
						weightPermCount += 1;
					}
				}
				ratio = (double)weightPermCount / totalPermCount;
			}
		}
		if (ratio >= 0.4 && ratio < 1) {
			risk *= 1 + ratio;
		}
		Collections.sort(permsList);

		return new String[]{String.format("%.2f", risk), packageName, firstInstall, lastMod,
				String.join("\n", permsList)};
	}
	// Reads whitelist from file and adds to map
	Map<String, String> readWhitelist(File wlTxt) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(wlTxt));
		String line;
		String[] result;
		Map<String, String> map = new HashMap<>();

		while ((line = br.readLine()) != null) {
			result = line.split(",");
			map.put(result[0], result[1]);
		}
		br.close();

		return map;
	}
	// Reads permission descriptions from file and adds to map
	Map<String, String> readPermissionDescriptions(File permDescTxt) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(permDescTxt));
		String line;
		String[] result;
		Map<String, String> map = new HashMap<>();

		while ((line = br.readLine()) != null) {
			result = line.split(",");
			map.put(result[0], result[1]);
		}
		br.close();

		return map;
	}
	
	public String[] getListData() {
		return listData;
	}

	public Map<String, String> getWhiteList() {
		return whitelist;
	}

	public Map<String, String> getPermissionDescriptions() {
		return permDesc;
	}

	public Map<String, Permissions> getPermWeightMap() {
		return permWeightMap;
	}
}
