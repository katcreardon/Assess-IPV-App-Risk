package androidPermissions;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import org.apache.commons.collections4.ListUtils;

public class AnalyzeAndroidPermissions {
    static HashSet<String> normal = new HashSet<>();
    static HashSet<String> dangerous = new HashSet<>();
    static HashSet<String> signature = new HashSet<>();
    static HashSet<String> removed = new HashSet<>();
    static HashSet<String> other = new HashSet<>();
    static File[] spyFiles = new File("src/res/spyware").listFiles();
    static File[] popFiles = new File("src/res/popular").listFiles();
    static File permFile = new File("src/res/permissions/allPermissions.txt");
    static File spyData = new File("src/res/permissions/spywarePermissions.txt");
    static File popData = new File("src/res/permissions/popularPermissions.txt");
    static File permByAppFile = new File("src/res/permissions/permissionsByApp.txt");
    static Map<String, Permissions> spyMap = new HashMap<>();
    static Map<String, Permissions> popMap = new HashMap<>();
    static File spyCount = new File("src/res/permissions/spywarePermissionsCount.txt");
    static File popCount = new File("src/res/permissions/popPermissionsCount.txt");
    static File sharedPerms = new File("src/res/permissions/sharedPermissions.txt");
    static File uniquePerms = new File("src/res/permissions/uniquePermissions.txt");

    public static void main(String[] args) {
        try {
            getPermissions(spyFiles, spyData, permByAppFile);
            getPermissions(popFiles, popData, permByAppFile);
            getCategories();
            List<Entry<String, Permissions>> sortedSpy = countPermissions(spyData, spyMap, spyCount);
            List<Entry<String, Permissions>> sortedPop = countPermissions(popData, popMap, popCount);
            comparePermissions(sortedSpy, sortedPop);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Reads all permissions from set of apps (spyware or popular) and writes to file
    static void getPermissions(File[] files, File dataTxt, File labeledTxt) throws IOException {
        String line;
        String prefix = "<uses-permission android:name=";
        BufferedWriter bw1 = new BufferedWriter(new FileWriter(dataTxt));
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(labeledTxt, true));

        for (File file : files) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            // Used to filter out repeated permissions within the same XML file
            HashSet<String> uniquePerms = new HashSet<>();
            bw2.write(file.toString() + "\n");
            while ((line = br.readLine()) != null) {
                if (line.contains(prefix)) {
                    String[] result = line.split("\"");
                    if (!uniquePerms.contains(result[1])) {
                        bw1.write(result[1] + "\n");
                        bw2.write(result[1] + "\n");
                        uniquePerms.add(result[1]);
                    }
                }
            }
            bw2.write("\n");
            br.close();
        }
        bw1.close();
        bw2.close();
    }
    // Reads categorized permissions (normal, dangerous, signature, or removed) and adds to set for use in program
    static void getCategories() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(permFile));
        String line;

        if ((br.readLine()).equals("Dangerous")) {
            while (!(line = br.readLine()).equals("")) {
                dangerous.add(line);
            }
        }
        if ((br.readLine()).equals("Normal")) {
            while (!(line = br.readLine()).equals("")) {
                normal.add(line);
            }
        }
        if ((br.readLine()).equals("Signature")) {
            while (!(line = br.readLine()).equals("")) {
                signature.add(line);
            }
        }
        if ((br.readLine()).equals("Removed")) {
            while (!(line = br.readLine()).equals("")) {
                removed.add(line);
            }
        }
        if ((br.readLine()).equals("Other")) {
            while ((line = br.readLine()) != null) {
                other.add(line);
            }
        }
        br.close();
    }
    // Counts repeated permissions and outputs to file sorted by type and descending count
    static List<Entry<String, Permissions>> countPermissions(File txt, Map<String, Permissions> map, File countTxt)
            throws IOException {
        String line;
        boolean isNormal = false;
        boolean isDangerous = false;
        boolean isSignature = false;
        boolean isRemoved = false;
        boolean isOther = false;
        BufferedReader br = new BufferedReader(new FileReader(txt));
        BufferedWriter bw = new BufferedWriter(new FileWriter(countTxt));

        while ((line = br.readLine()) != null) {
            if (map.containsKey(line)) {
                map.get(line).increaseCount();
            } else {
                if (normal.contains(line)) {
                    map.put(line, new Permissions(1, "normal"));
                    isNormal = true;
                }
                if (!isNormal) {
                    if (dangerous.contains(line)) {
                        map.put(line, new Permissions(1, "dangerous"));
                        isDangerous = true;
                    }
                }
                if (!isNormal && !isDangerous) {
                    if (signature.contains(line)) {
                        map.put(line, new Permissions(1, "signature"));
                        isSignature = true;
                    }
                }
                if (!isNormal && !isDangerous && !isSignature) {
                    if (removed.contains(line)) {
                        map.put(line, new Permissions(1, "removed"));
                        isRemoved = true;
                    }
                }
                if (!isNormal && !isDangerous && !isSignature && !isRemoved) {
                    if (other.contains(line)) {
                        map.put(line, new Permissions(1, "other"));
                        isOther = true;
                    }
                }
                if (!isNormal && !isDangerous && !isSignature && !isRemoved && !isOther) {
                    map.put(line, new Permissions(1, "unknown"));
                }
                isNormal = false;
                isDangerous = false;
                isSignature = false;
                isRemoved = false;
                isOther = false;
            }
        }
        br.close();

        List<Entry<String, Permissions>> sortedEntries = new ArrayList<>(map.entrySet());
        Collections.sort(sortedEntries, Entry.comparingByValue(Comparator.comparing(Permissions::getType)
                .thenComparing(Permissions::getCount, Comparator.reverseOrder())));

        for (Entry<String, Permissions> entry : sortedEntries) {
            int count = entry.getValue().getCount();
            String type = entry.getValue().getType();
            String name = entry.getKey();
            double percentIncl = ((double)count / 20) * 100;

            bw.write(count + " " + type + " " + name + " " + String.format("%,.0f", percentIncl) + "%\n");
        }
        bw.close();

        return sortedEntries;
    }
    // Returns shared and unique permissions
    static void comparePermissions(List<Entry<String, Permissions>> sp, List<Entry<String, Permissions>> pp)
            throws IOException {
        BufferedWriter bw1 = new BufferedWriter(new FileWriter(sharedPerms));
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(uniquePerms));
        List<String> spyList = new ArrayList<>();
        List<String> popList = new ArrayList<>();
        List<String> sharedList;
        List<String> uniqueSpyList;
        List<String> uniquePopList;
        String type;
        double percentIncl;
        double spyPer;
        double popPer;

        for (Entry<String, Permissions> e : sp) {
            spyList.add(e.getKey());
        }

        for (Entry<String, Permissions> e : pp) {
            popList.add(e.getKey());
        }

        sharedList = ListUtils.intersection(spyList, popList);
        uniqueSpyList = ListUtils.subtract(spyList, popList);
        uniquePopList = ListUtils.subtract(popList, spyList);
        Collections.sort(sharedList);
        Collections.sort(uniqueSpyList);
        Collections.sort(uniquePopList);

        for (String e : sharedList) {
            type = spyMap.get(e).getType();
            spyPer = ((double)(spyMap.get(e).getCount()) / 20) * 100;
            popPer = ((double)(popMap.get(e).getCount()) / 20) * 100;
            percentIncl = ((double)(spyMap.get(e).getCount() + popMap.get(e).getCount()) / 40) * 100;
            bw1.write(type + " " + e + " " + String.format("%,.0f", spyPer) + "% "
                    + String.format("%,.0f", popPer) + "% " + String.format("%,.0f", percentIncl) +"%\n");
        }

        bw2.write("Spyware\n");
        for (String e : uniqueSpyList) {
            type = spyMap.get(e).getType();
            percentIncl = ((double)spyMap.get(e).getCount() / 20) * 100;
            bw2.write(type + " " + e + " " + String.format("%,.0f", percentIncl) +"%\n");
        }
        bw2.write("\nPopular\n");
        for (String e : uniquePopList) {
            type = popMap.get(e).getType();
            percentIncl = ((double)popMap.get(e).getCount() / 20) * 100;
            bw2.write(type + " " + e + " " + String.format("%,.0f", percentIncl) +"%\n");
        }

        bw1.close();
        bw2.close();
    }
}
